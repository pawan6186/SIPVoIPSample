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

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnInstantMessageParam;
import org.pjsip.pjsua2.OnRegStateParam;

import java.util.ArrayList;


public class WeTalkAccount extends Account
{
    public ArrayList<WeTalkBuddy> buddyList = new ArrayList<WeTalkBuddy>();
    public AccountConfig cfg;

    WeTalkAccount(AccountConfig config)
    {
	super();
	cfg = config;
    }

    public WeTalkBuddy addBuddy(BuddyConfig bud_cfg)
    {
	/* Create Buddy */
	WeTalkBuddy bud = new WeTalkBuddy(bud_cfg);
	try {
	    bud.create(this, bud_cfg);
	} catch (Exception e) {
	    bud.delete();
	    bud = null;
	}

	if (bud != null) {
	    buddyList.add(bud);
	    if (bud_cfg.getSubscribe())
		try {
		    bud.subscribePresence(true);
	    } catch (Exception e) {}
	}

	return bud;
    }

    public void delBuddy(WeTalkBuddy buddy)
    {
	buddyList.remove(buddy);
	buddy.delete();
    }

    public void delBuddy(int index)
    {
	WeTalkBuddy bud = buddyList.get(index);
	buddyList.remove(index);
	bud.delete();
    }

    @Override
    public void onRegState(OnRegStateParam prm)
    {
        WeTalk.observer.notifyRegState(prm.getCode(), prm.getReason(),
				      prm.getExpiration());
    }

    @Override
    public void onIncomingCall(OnIncomingCallParam prm)
    {
	System.out.println("======== Incoming call ======== ");
	WeTalkCall call = new WeTalkCall(this, prm.getCallId());
	WeTalk.observer.notifyIncomingCall(call);
    }

    @Override
    public void onInstantMessage(OnInstantMessageParam prm)
    {
	System.out.println("======== Incoming pager ======== ");
	System.out.println("From     : " + prm.getFromUri());
	System.out.println("To       : " + prm.getToUri());
	System.out.println("Contact  : " + prm.getContactUri());
	System.out.println("Mimetype : " + prm.getContentType());
	System.out.println("Body     : " + prm.getMsgBody());
    }
}


