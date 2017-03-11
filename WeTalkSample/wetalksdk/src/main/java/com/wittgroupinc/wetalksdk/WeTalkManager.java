package com.wittgroupinc.wetalksdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.wittgroupinc.wetalksdk.models.WeTalkUser;
import com.wittgroupinc.wetalksdk.utils.LogUtil;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSetting;
import org.pjsip.pjsua2.StringVector;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;

/**
 * Created by Pawan Gupta on 29-09-2016.
 */

public class WeTalkManager implements IWeTalkManager, WeTalkObserver {

    private static WeTalkManager instance = null;
    private static AccountConfig accCfg = null;
    public static WeTalk app;
    private static WeTalkAccount account;
    private WeTalkCallBack callBack;
    private String lastRegStatus = "";
    private WeTalkCall currentCall;
    private WeTalkSessionCallBack sessionCallBack;


    private WeTalkManager() {
    }

    public static WeTalkManager getInstance() {
        if (instance == null) {
            instance = new WeTalkManager();
        }
        return instance;
    }


    public void initialize(Context context) {
        app = new WeTalk();
        if (false &&
                (context.getApplicationInfo().flags &
                        ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }
        app.init(this, context.getFilesDir().getAbsolutePath());
        if (app.accList.size() == 0) {
            accCfg = new AccountConfig();
            accCfg.setIdUri("sip:localhost");
            accCfg.getNatConfig().setIceEnabled(true);
            account = app.addAcc(accCfg);
        } else {
            account = app.accList.get(0);
            accCfg = account.cfg;
        }
    }


    @Override
    public boolean registerUser(String username, String password, WeTalkCallBack callback) {
        LogUtil.log(username + "/" + password);
        instance.callBack = callback;
        accCfg = new AccountConfig();
        String acc_id = String.format(WeTalkConfig.ACCOUNT_ID_URL, username, WeTalkConfig.DOMAIN);
        String registrar = String.format(WeTalkConfig.ACCOUNT_ID_URL, username, WeTalkConfig.DOMAIN);
        ;
        String proxy = WeTalkConfig.PROXY;
        accCfg.setIdUri(acc_id);
        accCfg.getRegConfig().setRegistrarUri(registrar);
        AuthCredInfoVector creds = accCfg.getSipConfig().getAuthCreds();
        creds.clear();
        if (username.length() != 0) {
            creds.add(new AuthCredInfo("Digest", "*", username, 0, password));
        }
        StringVector proxies = accCfg.getSipConfig().getProxies();
        proxies.clear();
        if (proxy.length() != 0) {
            proxies.add(proxy);
        }

			    	/* Enable ICE */
        accCfg.getNatConfig().setIceEnabled(true);

					/* Finally */
        lastRegStatus = "";
        try {
            account.modify(accCfg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean registerUser(WeTalkUser user, WeTalkCallBack callBack) {
        return registerUser(user.getUsername(), user.getPassword(), callBack);

    }

    @Override
    public void addSessionListener(WeTalkSessionCallBack callBack) {
        if (instance != null) {
            instance.sessionCallBack = callBack;
        }
    }

    @Override
    public void removeSessionListener() {
        if (instance != null) {
            instance.sessionCallBack = null;
        }
    }


    public void makeCall(String id) {
        if (id == null || id.isEmpty())
            return;
        String uri = String.format(WeTalkConfig.ACCOUNT_ID_URL, id, WeTalkConfig.DOMAIN);
        /* Only one call at anytime */
        if (currentCall != null) {
            return;
        }
        WeTalkCall call = new WeTalkCall(account, -1);
        CallOpParam prm = new CallOpParam();
        CallSetting opt = prm.getOpt();
        opt.setAudioCount(1);
        opt.setVideoCount(0);
        try {
            call.makeCall(uri, prm);
        } catch (Exception e) {
            call.delete();
            return;
        }
        currentCall = call;

    }



    public void hangupCall(WeTalkCall call) {


        if (call != null) {
            CallOpParam prm = new CallOpParam();
            prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
            try {
               call.hangup(prm);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void acceptCall(WeTalkCall call){
        CallOpParam prm = new CallOpParam(true);
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
        try {
            call.answer(prm);
        } catch (Exception e) {
            System.out.println(e);
        }

    }
    public void rejectCall(WeTalkCall call){
        if (call != null) {
            CallOpParam prm = new CallOpParam();
            prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
            try {
                call.hangup(prm);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void notifyRegState(pjsip_status_code code, String reason, int expiration) {
        Log.d("Pawan", reason);
        int requestCode = 0;
        String msg = "";
        if (expiration == 0) {
            msg += "Unregistration";
            requestCode = WeTalk.RequestCode.UNREGISTER;
        } else {
            msg += "Registration";
            requestCode = WeTalk.RequestCode.REGISTER;
        }
        if (code.swigValue() / 100 == 2) {
            msg += " successful";
            instance.callBack.onSuccess(requestCode, msg);
        } else {
            msg += " failed: " + reason;
            instance.callBack.onFailure(requestCode, msg);
        }

    }

    @Override
    public void notifyIncomingCall(WeTalkCall call) {
        instance.sessionCallBack.incoming(1, call);
    }


    @Override
    public void notifyCallMediaState(WeTalkCall call) {
    }

    @Override
    public void notifyBuddyState(WeTalkBuddy buddy) {
    }

    public class MSG_TYPE {
        public final static int INCOMING_CALL = 1;
        public final static int CALL_STATE = 2;
        public final static int REG_STATE = 3;
        public final static int BUDDY_STATE = 4;
        public final static int CALL_MEDIA_STATE = 5;
    }


    @Override
    public void notifyCallState(WeTalkCall call) {
        if (currentCall == null || call.getId() != currentCall.getId())
            return;
        CallInfo ci;
        try {
            ci = call.getInfo();
        } catch (Exception e) {
            ci = null;
        }
        if (ci != null) {
            if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                currentCall = null;
                instance.sessionCallBack.callDisconnected(1, call);
            } else if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                instance.sessionCallBack.callAccepted(1, call);
            } else if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CALLING) {
                instance.sessionCallBack.callRinging(1, call);
            } else if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_INCOMING) {
                instance.sessionCallBack.incoming(1, call);
            }
        }
        // currentCall = null;
    }


}
