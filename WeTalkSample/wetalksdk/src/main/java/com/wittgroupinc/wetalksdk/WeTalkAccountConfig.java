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

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.ContainerNode;

import java.util.ArrayList;


public class WeTalkAccountConfig
{
    public AccountConfig accCfg = new AccountConfig();
    public ArrayList<BuddyConfig> buddyCfgs = new ArrayList<BuddyConfig>();

    public void readObject(ContainerNode node)
    {
	try {
	    ContainerNode acc_node = node.readContainer("Account");
	    accCfg.readObject(acc_node);
	    ContainerNode buddies_node = acc_node.readArray("buddies");
	    buddyCfgs.clear();
	    while (buddies_node.hasUnread()) {
		BuddyConfig bud_cfg = new BuddyConfig(); 
		bud_cfg.readObject(buddies_node);
		buddyCfgs.add(bud_cfg);
	    }
	} catch (Exception e) {}
    }

    public void writeObject(ContainerNode node)
    {
	try {
	    ContainerNode acc_node = node.writeNewContainer("Account");
	    accCfg.writeObject(acc_node);
	    ContainerNode buddies_node = acc_node.writeNewArray("buddies");
	    for (int j = 0; j < buddyCfgs.size(); j++) {
		buddyCfgs.get(j).writeObject(buddies_node);
	    }
	} catch (Exception e) {}
    }
}


