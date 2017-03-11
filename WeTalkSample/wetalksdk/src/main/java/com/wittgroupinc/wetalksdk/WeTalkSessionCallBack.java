package com.wittgroupinc.wetalksdk;

/**
 * Created by AjayKumar on 10/10/16.
 */

public interface WeTalkSessionCallBack {
    public void callDisconnected(int code, WeTalkCall call);
    public void callAccepted(int code, WeTalkCall call);
    public void callHangUp(int code, WeTalkCall call);
    public void callRinging(int code, WeTalkCall call);
    public void callConnected(int code, WeTalkCall call);
    public void incoming(int code, WeTalkCall call);
}
