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

import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallMediaInfoVector;
import org.pjsip.pjsua2.Media;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.VideoPreview;
import org.pjsip.pjsua2.VideoWindow;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsua2;
import org.pjsip.pjsua2.pjsua_call_media_status;


public class WeTalkCall extends Call
{

    public VideoWindow vidWin;
    public VideoPreview vidPrev;

    WeTalkCall(WeTalkAccount acc, int call_id)
    {
	super(acc, call_id);
	vidWin = null;
    }

    @Override
    public void onCallState(OnCallStateParam prm)
    {
	    WeTalk.observer.notifyCallState(this);
	    try {
		CallInfo ci = getInfo();
		if (ci.getState() == 
		    pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED)
		{
		    this.delete();
		}
	    } catch (Exception e) {
		return;
	    }
    }

    @Override
    public void onCallMediaState(OnCallMediaStateParam prm)
    {
	CallInfo ci;
	try {
	    ci = getInfo();
	} catch (Exception e) {
	    return;
	}

	CallMediaInfoVector cmiv = ci.getMedia();

	for (int i = 0; i < cmiv.size(); i++) {
	    CallMediaInfo cmi = cmiv.get(i);
	    if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
		(cmi.getStatus() == 
		 	pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE ||
		 cmi.getStatus() == 
			pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD))
	    {
		// unfortunately, on Java too, the returned Media cannot be
		// downcasted to AudioMedia 
		Media m = getMedia(i);
		AudioMedia am = AudioMedia.typecastFromMedia(m);

		// connect ports
		try {
		    WeTalk.ep.audDevManager().getCaptureDevMedia().
							    startTransmit(am);
		    am.startTransmit(WeTalk.ep.audDevManager().
				     getPlaybackDevMedia());
		} catch (Exception e) {
		    continue;
		}
	    } else if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_VIDEO &&
		       cmi.getStatus() == 
			    pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE &&
		       cmi.getVideoIncomingWindowId() != pjsua2.INVALID_ID)
	    {
		vidWin = new VideoWindow(cmi.getVideoIncomingWindowId());
		vidPrev = new VideoPreview(cmi.getVideoCapDev());
	    }
	}

	WeTalk.observer.notifyCallMediaState(this);
    }
}


