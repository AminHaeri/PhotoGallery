package com.example.photogallery.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class PhotoPageActivity extends SingleFragmentActivity {

    private static final String PHOTO_URI = "com.example.photogallery.controller.PHOTO_URI";

    public static String getPhotoUri() {
        return PHOTO_URI;
    }

    public static Intent newIntent(Context context, Uri uri) {
        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.putExtra(PHOTO_URI, uri);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        return PhotoPageFragment.newInstance((Uri) getIntent().getParcelableExtra(PHOTO_URI));
    }
}
