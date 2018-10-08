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

    /**
     * 只有java子线程可以回调
     * c创建线程调用AttachCurrentThread也行
     */
    public static void onCrash() {
        Log.d(LOG_TAG, "onCrash");
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        Log.d(LOG_TAG, "onCrash:activeCount=" + group.activeCount());
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads);
        boolean havaSelf = false;
        for (int i = 0; i < threads.length; i++) {
            Thread thread = threads[i];
            if (thread == Thread.currentThread()) {
                havaSelf = true;
            }
            printfThread(thread, thread == Thread.currentThread());
        }
        if (!havaSelf) {
            printfThread(Thread.currentThread(), true);
        }
    }

    private static void printfThread(Thread thread, boolean self) {
        Log.d(LOG_TAG, "printfThread:>>>>>>>>>>>>");
        StackTraceElement[] stes = thread.getStackTrace();
        Log.d(LOG_TAG, "printfThread:thread=" + thread + ",self=" + self);
        for (int i = 0; i < stes.length; i++) {
            StackTraceElement ste = stes[i];
            Log.d(LOG_TAG, "printfThread:ste=" + ste);
        }
        Log.d(LOG_TAG, "printfThread:<<<<<<<<<<<<");
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

    public native void crashThread();

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
