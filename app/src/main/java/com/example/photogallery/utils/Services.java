package com.example.photogallery.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.photogallery.PhotoGalleryActivity;
import com.example.photogallery.R;
import com.example.photogallery.model.GalleryItem;
import com.example.photogallery.network.FlickrFetcher;
import com.example.photogallery.prefs.QueryPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Services {

    public static void pollServerAndSendNotification(Context context, String TAG) {
        String query = QueryPreferences.getStoredQuery(context);
        String lastId = QueryPreferences.getLastId(context);

        List<GalleryItem> galleryItems = new ArrayList<>();
        try {
            if (query == null) {
                galleryItems = new FlickrFetcher().fetchPopular();
            } else {
                galleryItems = new FlickrFetcher().searchGalleryItems(query);
            }
        } catch (IOException e) {
            Log.e(TAG, "error in get from server: " , e);
        }

        String id = galleryItems.get(0).getId();
        if (id.equals(lastId)) {
            //old result
            Log.d(TAG, "old result");
        } else {
            //new result
            Log.d(TAG, "new result");

            Intent i = PhotoGalleryActivity.newIntent(context);
            PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);

            String channelId = context.getString(R.string.channel_id);
            Notification notification = new NotificationCompat.Builder(context, channelId)
                    .setContentTitle(context.getString(R.string.new_pictures_title))
                    .setContentText(context.getString(R.string.new_pictures_text))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat nmc = NotificationManagerCompat.from(context);
            nmc.notify(0, notification);

        }
        QueryPreferences.setLastId(context, id);
    }
}
