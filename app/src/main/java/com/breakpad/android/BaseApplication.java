package com.breakpad.android;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        CrashReport.initCrashReport(getApplicationContext(), "", true);
    }
}
