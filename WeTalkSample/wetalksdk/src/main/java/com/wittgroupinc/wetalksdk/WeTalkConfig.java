package com.wittgroupinc.wetalksdk;

/**
 * Created by Pawan Gupta on 29-09-2016.
 */

public class WeTalkConfig {
    public static boolean logEnabled = true;
    public static final String DOMAIN = "onblickcall.tk";
    public static final String ACCOUNT_ID_URL = "sip:%s@%s"; //username, domain.
    public static final String PROXY = String.format("sip:%s",DOMAIN); //domain.
    public final static String CONFIG_FILE_NAME = "pjsua2.json";
    public final static int SIP_PORT  = 5060;
    public final static int LOG_LEVEL = 4;
}
