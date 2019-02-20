package com.example.photogallery.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * This is for testing
 */
public class MyService extends Service {

    public static final String TAG = "MyService";
    private MyBinder mMyBinder = new MyBinder();

    public static Intent newIntent(Context context) {
        return new Intent(context, MyService.class);
    }

    public MyService() {
        Log.d(TAG, "Constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        Log.d(TAG, "startId: " + startId);

        if (1 == 1) {
            stopSelf(23);
        }

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMyBinder;
    }

    public String getServiceName() {
        return "Hi Service!!";
    }

    private class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }
}
