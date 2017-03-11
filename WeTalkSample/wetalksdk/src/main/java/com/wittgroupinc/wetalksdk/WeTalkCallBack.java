package com.wittgroupinc.wetalksdk;

/**
 * Created by AjayKumar on 10/10/16.
 */

public interface WeTalkCallBack {
    public void onSuccess(int code, Object obj);
    public void onFailure(int code, Object obj);
}
