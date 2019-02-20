package com.example.photogallery.receiver;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.photogallery.utils.Services;

import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received in NotificationReceiver");

        if (getResultCode() == Activity.RESULT_OK) {
            int requestCode = intent.getIntExtra(Services.getExtraRequestCode(), 0);
            Notification notification = intent.getParcelableExtra(Services.getExtraNotification());

            NotificationManagerCompat nmc = NotificationManagerCompat.from(context);
            nmc.notify(requestCode, notification);
        }


    }
}
