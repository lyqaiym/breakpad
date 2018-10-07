package com.breakpad.nativecreash;

import android.util.Log;

public class NativeCrash {
    private static final String LOG_TAG = "NativeCrash";
    private static NativeCrash instance;

    private NativeCrash() {
    }

    public static NativeCrash getInstance() {
        if (instance == null) {
            instance = new NativeCrash();
        }
        return instance;
    }

    public boolean initJava(String path) {
        if (loaded) {
            try {
                init(path);
                return true;
            } catch (UnsatisfiedLinkError e) {
                Log.e(LOG_TAG, "init", e);
            }
        }
        return false;
    }

    public native void crash();

    private native void init(String path);

    static boolean loaded;

    static {
        try {
            System.loadLibrary("test_google_breakpad");
            loaded = true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "loadLibrary", e);
        }
    }
}
