package com.breakpad.android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.breakpad.nativecreash.NativeCrash;
import com.netease.LDNetDiagnoService.LDNetTraceRoute;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int check = checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, android.os.Process.myPid(),
                    android.os.Process.myUid());
            if (check != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, 100);
            }
        }
        Button bt_crash_init = (Button) findViewById(R.id.bt_crash_init);
        bt_crash_init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final File file = new File(getCacheDir(), "crash");
                final File file = new File(Environment.getExternalStorageDirectory(), "crash");
                if (!file.exists()) {
                    file.mkdirs();
                }
                File[] fs = file.listFiles();
                if (fs != null) {
                    Log.d(TAG, "onCreate:cacheDir=" + file + ",size=" + fs.length);
                } else {
                    Log.d(TAG, "onCreate:cacheDir=" + file + ",size=null");
                }
                boolean init = NativeCrash.getInstance().initJava(file.getPath());
                Log.d(TAG, "onClick:init=" + init);
                Toast.makeText(MainActivity.this, init ? "初始化成功" : "初始化失败", Toast.LENGTH_SHORT).show();
            }
        });
        Button bt_bugly_init = (Button) findViewById(R.id.bt_bugly_init);
        bt_bugly_init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashReport.initCrashReport(getApplicationContext(), "", true);
            }
        });
        Button bt_self_other = (Button) findViewById(R.id.bt_self_other);
        bt_self_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NativeCrash.getInstance().crash();
            }
        });
        Button bt_self_other_thread_java = (Button) findViewById(R.id.bt_self_other_thread_java);
        bt_self_other_thread_java.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        NativeCrash.getInstance().crash();
                    }
                }.start();
            }
        });
        Button bt_self_other_thread_c = (Button) findViewById(R.id.bt_self_other_thread_c);
        bt_self_other_thread_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NativeCrash.getInstance().crashThread();
            }
        });
        Button bt_test_other = (Button) findViewById(R.id.bt_test_other);
        bt_test_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LDNetTraceRoute ldNetTraceRoute = LDNetTraceRoute.getInstance();
                ldNetTraceRoute.startJNICTraceRoute("");
            }
        });
        Button bt_test_bitmap = (Button) findViewById(R.id.bt_test_bitmap);
        bt_test_bitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                findViewById(R.id.crash_surface).setVisibility(View.VISIBLE);
            }
        });
    }
}
