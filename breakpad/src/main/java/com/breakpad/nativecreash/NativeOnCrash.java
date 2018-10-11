package com.breakpad.nativecreash;

import android.util.Log;

public class NativeOnCrash {
    private static final String LOG_TAG = "NativeOnCrash";

    static {
        Log.d(LOG_TAG, "NativeOnCrash:static");
    }

    /**
     * 有的系统主线程能调到，有的java子线程可以回调
     * c创建线程调用AttachCurrentThread也行
     */
    public static void onCrash(String path) {
        Log.d(LOG_TAG, "onCrash:path=" + path);
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
            Log.d(LOG_TAG, "onCrash:havaSelf=false");
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

}
