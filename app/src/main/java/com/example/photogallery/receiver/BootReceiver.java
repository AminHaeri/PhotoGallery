package com.example.photogallery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.photogallery.prefs.QueryPreferences;
import com.example.photogallery.service.PollService;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received Broadcast Intent: " + intent);

        Boolean isAlarmOn = QueryPreferences.isAlarmOn(context);

        if (isAlarmOn)
            PollService.setServiceAlarm(context, true);
    }
}
