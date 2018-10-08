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

    public static void onCrash() {
        Log.d(LOG_TAG, "onCrash");
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        Log.d(LOG_TAG, "onCrash:activeCount=" + group.activeCount());
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads);
        for (int i = 0; i < threads.length; i++) {
            Thread thread = threads[i];
            printfThread(thread);
        }
    }

    private static void printfThread(Thread thread) {
        StackTraceElement[] stes = thread.getStackTrace();
        Log.d(LOG_TAG, "printfThread:thread=" + thread + ",stes=" + stes.length);
        for (int i = 0; i < stes.length; i++) {
            StackTraceElement ste = stes[i];
            Log.d(LOG_TAG, "printfThread:ste=" + ste);
        }
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
