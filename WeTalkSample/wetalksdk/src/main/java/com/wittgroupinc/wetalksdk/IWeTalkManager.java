package com.wittgroupinc.wetalksdk;

import com.wittgroupinc.wetalksdk.models.WeTalkUser;

/**
 * Created by Pawan Gupta on 29-09-2016.
 */

public interface IWeTalkManager {
    public boolean registerUser(String username, String password,WeTalkCallBack callBack);
    public void makeCall(String username);
    public void acceptCall(WeTalkCall call);
    public void rejectCall(WeTalkCall call);
    public boolean registerUser(WeTalkUser user, WeTalkCallBack callBack);
    public void addSessionListener(WeTalkSessionCallBack callBack);
    public void hangupCall(WeTalkCall call);
    public void removeSessionListener();


}
