/* $Id: MyApp.java 5361 2016-06-28 14:32:08Z nanang $ */
/*
 * Copyright (C) 2013 Teluu Inc. (http://www.teluu.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.wittgroupinc.wetalksdk;

import java.io.File;
import java.util.ArrayList;

import org.pjsip.pjsua2.*;


public class WeTalk {
    static {
	try{
	    System.loadLibrary("openh264");
            // Ticket #1937: libyuv is now included as static lib
            //System.loadLibrary("yuv");
	} catch (UnsatisfiedLinkError e) {
	    System.out.println("UnsatisfiedLinkError: " + e.getMessage());
	    System.out.println("This could be safely ignored if you " +
			       "don't need video.");
	}
	System.loadLibrary("pjsua2");
	System.out.println("Library loaded");
    }

    public static Endpoint ep = new Endpoint();
    public static WeTalkObserver observer;
    public ArrayList<WeTalkAccount> accList = new ArrayList<WeTalkAccount>();

    private ArrayList<WeTalkAccountConfig> accCfgs =
					  new ArrayList<WeTalkAccountConfig>();
    private EpConfig epConfig = new EpConfig();
    private TransportConfig sipTpConfig = new TransportConfig();
    private String appDir;

    /* Maintain reference to log writer to avoid premature cleanup by GC */
    private WeTalkLogWriter logWriter;



    public void init(WeTalkObserver obs, String app_dir)
    {
	init(obs, app_dir, false);
    }

    public void init(WeTalkObserver obs, String app_dir,
					 boolean own_worker_thread)
    {
	observer = obs;
	appDir = app_dir;

	/* Create endpoint */
	try {
	    ep.libCreate();
	} catch (Exception e) {
	    return;
	}


	/* Load config */
	String configPath = appDir + "/" + WeTalkConfig.CONFIG_FILE_NAME;
	File f = new File(configPath);
	if (f.exists()) {
	    loadConfig(configPath);
	} else {
	    /* Set 'default' values */
	    sipTpConfig.setPort(WeTalkConfig.SIP_PORT);
	}

	/* Override log level setting */
	epConfig.getLogConfig().setLevel(WeTalkConfig.LOG_LEVEL);
	epConfig.getLogConfig().setConsoleLevel(WeTalkConfig.LOG_LEVEL);

	/* Set log config. */
	LogConfig log_cfg = epConfig.getLogConfig();
	logWriter = new WeTalkLogWriter();
	log_cfg.setWriter(logWriter);
	log_cfg.setDecor(log_cfg.getDecor() & 
			 ~(pj_log_decoration.PJ_LOG_HAS_CR.swigValue() | 
			 pj_log_decoration.PJ_LOG_HAS_NEWLINE.swigValue()));

	/* Set ua config. */
	UaConfig ua_cfg = epConfig.getUaConfig();
	ua_cfg.setUserAgent("Pjsua2 Android " + ep.libVersion().getFull());
	StringVector stun_servers = new StringVector();
	stun_servers.add("stun.pjsip.org");
	ua_cfg.setStunServer(stun_servers);
	if (own_worker_thread) {
	    ua_cfg.setThreadCnt(0);
	    ua_cfg.setMainThreadOnly(true);
	}

	/* Init endpoint */
	try {
	    ep.libInit(epConfig);
	} catch (Exception e) {
	    return;
	}

	/* Create transports. */
	try {
	    ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP,
			       sipTpConfig);
	} catch (Exception e) {
	    System.out.println(e);
	}

	try {
	    ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TCP,
			       sipTpConfig);
	} catch (Exception e) {
	    System.out.println(e);
	}

	/* Create accounts. */
	for (int i = 0; i < accCfgs.size(); i++) {
	    WeTalkAccountConfig my_cfg = accCfgs.get(i);

	    /* Customize account config */
	    my_cfg.accCfg.getNatConfig().setIceEnabled(true);
	    my_cfg.accCfg.getVideoConfig().setAutoTransmitOutgoing(true);
	    my_cfg.accCfg.getVideoConfig().setAutoShowIncoming(true);

	    WeTalkAccount acc = addAcc(my_cfg.accCfg);
	    if (acc == null)
		continue;

	    /* Add Buddies */
	    for (int j = 0; j < my_cfg.buddyCfgs.size(); j++) {
		BuddyConfig bud_cfg = my_cfg.buddyCfgs.get(j);
		acc.addBuddy(bud_cfg);
	    }
	}



	/* Start. */
	try {
	    ep.libStart();
	} catch (Exception e) {
	    return;
	}
    }

    public WeTalkAccount addAcc(AccountConfig cfg)
    {
	WeTalkAccount acc = new WeTalkAccount(cfg);
	try {
	    acc.create(cfg);
	} catch (Exception e) {
	    acc = null;
	    return null;
	}

	accList.add(acc);
	return acc;
    }

    public void delAcc(WeTalkAccount acc)
    {
	accList.remove(acc);
    }

    private void loadConfig(String filename)
    {
	JsonDocument json = new JsonDocument();

	try {
	    /* Load file */
	    json.loadFile(filename);
	    ContainerNode root = json.getRootContainer();

	    /* Read endpoint config */
	    epConfig.readObject(root);

	    /* Read transport config */
	    ContainerNode tp_node = root.readContainer("SipTransport");
	    sipTpConfig.readObject(tp_node);

	    /* Read account configs */
	    accCfgs.clear();
	    ContainerNode accs_node = root.readArray("accounts");
	    while (accs_node.hasUnread()) {
		WeTalkAccountConfig acc_cfg = new WeTalkAccountConfig();
		acc_cfg.readObject(accs_node);
		accCfgs.add(acc_cfg);
	    }
	} catch (Exception e) {
	    System.out.println(e);
	}

	/* Force delete json now, as I found that Java somehow destroys it
	* after lib has been destroyed and from non-registered thread.
	*/
	json.delete();
    }

    private void buildAccConfigs()
    {
	/* Sync accCfgs from accList */
	accCfgs.clear();
	for (int i = 0; i < accList.size(); i++) {
	    WeTalkAccount acc = accList.get(i);
	    WeTalkAccountConfig my_acc_cfg = new WeTalkAccountConfig();
	    my_acc_cfg.accCfg = acc.cfg;

	    my_acc_cfg.buddyCfgs.clear();
	    for (int j = 0; j < acc.buddyList.size(); j++) {
		WeTalkBuddy bud = acc.buddyList.get(j);
		my_acc_cfg.buddyCfgs.add(bud.cfg);
	    }

	    accCfgs.add(my_acc_cfg);
	}
    }

    private void saveConfig(String filename)
    {
	JsonDocument json = new JsonDocument();

	try {
	    /* Write endpoint config */
	    json.writeObject(epConfig);

	    /* Write transport config */
	    ContainerNode tp_node = json.writeNewContainer("SipTransport");
	    sipTpConfig.writeObject(tp_node);

	    /* Write account configs */
	    buildAccConfigs();
	    ContainerNode accs_node = json.writeNewArray("accounts");
	    for (int i = 0; i < accCfgs.size(); i++) {
		accCfgs.get(i).writeObject(accs_node);
	    }

	    /* Save file */
	    json.saveFile(filename);
	} catch (Exception e) {}

	/* Force delete json now, as I found that Java somehow destroys it
	* after lib has been destroyed and from non-registered thread.
	*/
	json.delete();
    }

    public void deinit()
    {
	String configPath = appDir + "/" + WeTalkConfig.CONFIG_FILE_NAME;
	saveConfig(configPath);

	/* Try force GC to avoid late destroy of PJ objects as they should be
	* deleted before lib is destroyed.
	*/
	Runtime.getRuntime().gc();

	/* Shutdown pjsua. Note that Endpoint destructor will also invoke
	* libDestroy(), so this will be a test of double libDestroy().
	*/
	try {
	    ep.libDestroy();
	} catch (Exception e) {}

	/* Force delete Endpoint here, to avoid deletion from a non-
	* registered thread (by GC?). 
	*/
	ep.delete();
	ep = null;
    }

	public static class RequestCode {
		public static int REGISTER=100;
		public static int UNREGISTER=101;

	}
}
