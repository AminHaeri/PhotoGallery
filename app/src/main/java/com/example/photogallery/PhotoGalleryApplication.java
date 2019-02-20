package com.example.photogallery;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.example.photogallery.events.NotificationEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.core.app.NotificationManagerCompat;

public class PhotoGalleryApplication extends Application {

    public static final boolean isBusEvent = true;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        EventBus.getDefault().unregister(this);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.channel_id);
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Subscribe(priority = 1, threadMode = ThreadMode.POSTING)
    public void showNotificationEventBus(NotificationEvent notificationEvent) {
        Log.i("EventBus", "showNotificationEventBus");

        NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
        nmc.notify(notificationEvent.getRequestCode(), notificationEvent.getNotification());
    }
}
