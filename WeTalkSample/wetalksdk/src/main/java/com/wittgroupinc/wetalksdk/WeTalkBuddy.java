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

import org.pjsip.pjsua2.Buddy;
import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.BuddyInfo;
import org.pjsip.pjsua2.pjsip_evsub_state;
import org.pjsip.pjsua2.pjsua_buddy_status;


public class WeTalkBuddy extends Buddy
{
    public BuddyConfig cfg;

    WeTalkBuddy(BuddyConfig config)
    {
	super();
	cfg = config;
    }

    String getStatusText()
    {
	BuddyInfo bi;

	try {
	    bi = getInfo();
	} catch (Exception e) {
	    return "?";
	}

	String status = "";
	if (bi.getSubState() == pjsip_evsub_state.PJSIP_EVSUB_STATE_ACTIVE) {
	    if (bi.getPresStatus().getStatus() ==
		pjsua_buddy_status.PJSUA_BUDDY_STATUS_ONLINE)
	    {
		status = bi.getPresStatus().getStatusText();
		if (status == null || status.length()==0) {
		    status = "Online";
		}
	    } else if (bi.getPresStatus().getStatus() == 
		       pjsua_buddy_status.PJSUA_BUDDY_STATUS_OFFLINE)
	    {
		status = "Offline";
	    } else {
		status = "Unknown";
	    }
	}
	return status;
    }

    @Override
    public void onBuddyState()
    {
	WeTalk.observer.notifyBuddyState(this);
    }

}


