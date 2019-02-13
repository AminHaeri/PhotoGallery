package com.example.photogallery.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "searchQuery";

    public static void setStoredQuery(Context context, String query) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        sharedPreferences
                .edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply();
    }

    public static String getStoredQuery(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString(PREF_SEARCH_QUERY, null);
    }
}
