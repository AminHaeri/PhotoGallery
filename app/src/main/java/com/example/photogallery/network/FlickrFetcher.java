package com.example.photogallery.network;

import android.net.Uri;
import android.util.Log;

import com.example.photogallery.model.Gallery;
import com.example.photogallery.model.GalleryItem;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetcher {

    private static final String TAG = "FlickrFetcher";

    private static final String API_KEY = "79b5c28546b0c0fd5a0bdc65ac9eab18";

    private Uri ENDPOINT = Uri.parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .appendQueryParameter("user_id", "34427466731@N01")
            .build();

    private enum FlickrMethods {
        POPULAR,
        SEARCH
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream inputStream = httpURLConnection.getInputStream();

            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(httpURLConnection.getResponseMessage() + " with: " +urlSpec);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int readSize = 0;

            while ((readSize = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readSize);
            }

            outputStream.close();
            return outputStream.toByteArray();

        } catch (MalformedURLException e) {
            Log.e(TAG, "malformed url: ", e);
        } finally {
            httpURLConnection.disconnect();
        }
        return null;
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> downloadGalleryItems(String url) throws IOException {
        String result = getUrlString(url);
        Log.d(TAG, "fetched: " + result);

        List<GalleryItem> galleryItems = new ArrayList<>();
        try {
            JSONObject jsonBody = new JSONObject(result);
            JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
            Gson gson = new Gson();
            Gallery gallery = gson.fromJson(photosJsonObject.toString(), Gallery.class);
            galleryItems = gallery.getGalleryItems();

            /*JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
            GalleryItem[] arrayGalleryItems = new Gson().fromJson(photoJsonArray.toString(), GalleryItem[].class);
            galleryItems = Arrays.asList(arrayGalleryItems);*/
//            for (int i = 0; i < photoJsonArray.length(); i++) {
//                JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
////
////                String id = photoJsonObject.getString("id");
////                String caption = photoJsonObject.getString("title");
////                String url_s = photoJsonObject.getString("url_s");
////
////                GalleryItem galleryItem = new GalleryItem(id, caption, url_s);
//
//                Gson gson = new Gson();
//                GalleryItem galleryItem = gson.fromJson(photoJsonObject.toString(), GalleryItem.class);
//                galleryItems.add(galleryItem);
//            }
        } catch (JSONException e) {
            Log.e(TAG, "fail to parse json ", e);
        }

        return galleryItems;
    }

    public List<GalleryItem> fetchPopular() throws IOException {
//        String url = buildUrl(FlickrMethods.POPULAR, null);
        return downloadGalleryItems(buildUrl(FlickrMethods.POPULAR, null));
    }

    public List<GalleryItem> searchGalleryItems(String query) throws IOException {
//        String url = buildUrl(FlickrMethods.SEARCH, query);
        return downloadGalleryItems(buildUrl(FlickrMethods.SEARCH, query));
    }

    private String buildUrl(FlickrMethods methods, String query) {
        Uri.Builder builder = ENDPOINT.buildUpon();
        switch (methods) {
            case POPULAR:
                builder.appendQueryParameter("method", "flickr.photos.getRecent");
                break;
            case SEARCH:
                builder.appendQueryParameter("method", "flickr.photos.search")
                        .appendQueryParameter("text", query);
                break;
            default:
                return null;
        }

        return builder.build().toString();
    }
}
