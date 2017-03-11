package com.wittgroupinc.wetalksdk;

import org.pjsip.pjsua2.pjsip_status_code;

/**
 * Created by AjayKumar on 10/6/16.
 */

public interface WeTalkObserver {
    abstract void notifyRegState(pjsip_status_code code, String reason,
                                 int expiration);
    abstract void notifyIncomingCall(WeTalkCall call);
    abstract void notifyCallState(WeTalkCall call);
    abstract void notifyCallMediaState(WeTalkCall call);
    abstract void notifyBuddyState(WeTalkBuddy buddy);
}
