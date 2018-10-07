package com.netease.LDNetDiagnoService;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过ping模拟traceroute过程
 *
 * @author panghui
 */
public class LDNetTraceRoute {
    private final String LOG_TAG = "LDNetTraceRoute";
    private static LDNetTraceRoute instance;

    private LDNetTraceRoute() {
    }

    public static LDNetTraceRoute getInstance() {
        if (instance == null) {
            instance = new LDNetTraceRoute();
        }
        return instance;
    }

    /**
     * 执行指定host的traceroute
     *
     * @param host
     * @return
     */
    public void startTraceRoute(String host) {
        if (loaded) {
            try {
                startJNICTraceRoute(host);
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 调用jni c函数执行traceroute过程
     */
    public native void startJNICTraceRoute(String traceCommand);

    static boolean loaded;

    static {
        try {
            System.loadLibrary("tracepath");
            loaded = true;
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
