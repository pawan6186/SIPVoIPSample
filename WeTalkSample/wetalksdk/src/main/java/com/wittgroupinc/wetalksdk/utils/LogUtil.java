package com.wittgroupinc.wetalksdk.utils;

import android.util.Log;

import com.wittgroupinc.wetalksdk.WeTalkConfig;

/**
 * Created by Pawan Gupta on 29-09-2016.
 */


public class LogUtil {
    private static String TAG = "WeTalk";

    private final static int DEBUG = 1;
    private final static int ERROR = 2;
    private final static int WARNING = 3;


    public static void log(String msg) {
        log(TAG, msg, DEBUG);
    }

    public static void log(String tag, String msg) {
        log(tag, msg, DEBUG);
    }

    private static void log(String tag, String msg, int type) {
        if (WeTalkConfig.logEnabled) {
            switch (type) {
                case ERROR:
                    Log.e(tag, msg);
                    break;
                case WARNING:
                    Log.w(tag, msg);
                    break;
                default:
                    Log.d(tag, msg);
            }

        }
    }

    public static void logError(String msg) {
        log(TAG, msg, ERROR);
    }

    public static void logError(String tag, String msg) {
        log(tag, msg, ERROR);
    }

    public static void logWarning(String msg) {
        log(TAG, msg, WARNING);
    }


    public static void logWarning(String tag, String msg) {
        if (WeTalkConfig.logEnabled) {
            log(tag, msg, WARNING);
        }
    }
}

