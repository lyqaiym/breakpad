package com.breakpad.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CrashSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    String TAG = "MainActivityLog";
    DrawThread drawThread;
    CreateThread createThread;
    Bitmap bitmap;

    public CrashSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_livevideo_learnfeedback_duration);
        if (drawThread != null) {
            drawThread.isRun = false;
        }
        if (createThread != null) {
            createThread.isRun = false;
        }
        drawThread = new DrawThread();
        drawThread.isRun = true;
        new Thread(drawThread).start();
        createThread = new CreateThread();
        createThread.isRun = true;
        new Thread(createThread).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        if (drawThread != null) {
            drawThread.isRun = false;
        }
        if (createThread != null) {
            createThread.isRun = false;
        }
    }

    class DrawThread implements Runnable {
        boolean isRun = false;

        @Override
        public void run() {
            while (isRun) {
                Canvas canvas = null;
                try {
                    canvas = getHolder().lockCanvas();
                    canvas.drawBitmap(bitmap, 0, 0, null);
                } catch (Exception e) {

                } finally {
                    if (canvas != null) {
                        try {
                            getHolder().unlockCanvasAndPost(canvas);
                        } catch (Exception e) {

                        }
                    }
                }
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CreateThread implements Runnable {
        boolean isRun = false;

        @Override
        public void run() {
            while (isRun) {
                bitmap.recycle();
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_livevideo_learnfeedback_duration);
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
