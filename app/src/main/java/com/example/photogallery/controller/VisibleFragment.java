package com.example.photogallery.controller;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photogallery.PhotoGalleryApplication;
import com.example.photogallery.events.NotificationEvent;
import com.example.photogallery.utils.Services;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 */
public class VisibleFragment extends Fragment {

    private static final String TAG = "VisibleFragment";
    private BroadcastReceiver mOnShowNotificationReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Visible Fragment Receiver");
            Toast.makeText(getActivity(), "Received intent: " + intent,
                    Toast.LENGTH_LONG).show();

            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    public VisibleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        //ugly ugly bad bad code**********************************
        if (PhotoGalleryApplication.isBusEvent) {
            EventBus.getDefault().register(this);
        } else {
            IntentFilter intentFilter = new IntentFilter(Services.getActionShowNotification());
            getActivity().registerReceiver(
                    mOnShowNotificationReceiver,
                    intentFilter,
                    Services.getPermPrivate(),
                    null);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        //ugly ugly bad bad code**********************************
        if (PhotoGalleryApplication.isBusEvent) {
            EventBus.getDefault().unregister(this);
        } else {
            getActivity().unregisterReceiver(mOnShowNotificationReceiver);
        }
    }

    @Subscribe(priority = 2, threadMode = ThreadMode.POSTING)
    public void cancelNotificationEventBus(NotificationEvent notificationEvent) {
        Log.i("EventBus", "cancelNotificationEventBus");
        EventBus.getDefault().cancelEventDelivery(notificationEvent);
    }
}
