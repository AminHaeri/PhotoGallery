package com.example.photogallery.controller;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.photogallery.ThumbnailDownloader;
import com.example.photogallery.service.PollService;
import com.example.photogallery.R;
import com.example.photogallery.model.GalleryItem;
import com.example.photogallery.network.FlickrFetcher;
import com.example.photogallery.prefs.QueryPreferences;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoGalleryFragment extends VisibleFragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;
    private List<GalleryItem> mGalleryItems = new ArrayList<>();

    private ThumbnailDownloader mThumbnailDownloader;

    public static PhotoGalleryFragment newInstance() {
        
        Bundle args = new Bundle();
        
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PhotoGalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader(responseHandler);
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();

        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener() {
            @Override
            public void onThumbnailDownloaded(Object target, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                ((PhotoHolder)target).bindDrawable(drawable);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mThumbnailDownloader.quit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mRecyclerView = view.findViewById(R.id.photo_gallery_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_photo_gallery, menu);
        MenuItem togglePollingItem = menu.findItem(R.id.menu_item_toggle_polling);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                QueryPreferences.setStoredQuery(getActivity(), query);
                updateItems();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });

        if (isScheduleOrServiceOn()) {
            togglePollingItem.setTitle(R.string.stop_polling);
        } else {
            togglePollingItem.setTitle(R.string.start_polling);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                return true;
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
//                PollService.setServiceAlarm(getActivity(),
//                        !PollService.isAlarmOn(getActivity()));

                setScheduleOrAlarm(!isScheduleOrServiceOn());
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupAdapter() {
        if (isAdded())
            mRecyclerView.setAdapter(new PhotoAdapter(mGalleryItems));
    }

    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(getActivity());
        new PhotoTask().execute(query);
    }

    private void setScheduleOrAlarm(boolean isOn) {
        PollService.setServiceAlarm(getActivity(), isOn);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PollJobService.scheduleService(getActivity(), isOn);
        } else {
            PollService.setServiceAlarm(getActivity(), isOn);
        }*/
    }

    private boolean isScheduleOrServiceOn() {
        return PollService.isAlarmOn(getActivity());
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return PollJobService.isScheduled(getActivity());
        } else {
            return PollService.isAlarmOn(getActivity());
        }*/
    }

    public class PhotoHolder extends RecyclerView.ViewHolder {

        private ImageView mGalleryImageView;
        private GalleryItem mGalleryItem;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);

            mGalleryImageView = itemView.findViewById(R.id.list_item_image_view);
            mGalleryImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = PhotoPageActivity.newIntent(getActivity(), mGalleryItem.getPhotoUri());
                    startActivity(intent);
                }
            });
        }

        public void bindGalleryItem(GalleryItem galleryItem) {
            mGalleryItem = galleryItem;
            mThumbnailDownloader.queueThumbnail(galleryItem.getUrl(), this);

//            Picasso.get()
//                    .load(mGalleryItem.getUrl())
//                    .placeholder(R.drawable.ic_placeholder_image_android)
//                    .into(mGalleryImageView);
        }

        public void bindDrawable(Drawable drawable) {
            mGalleryImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        List<GalleryItem> mGalleryItems;

        public void setGalleryItems(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_gallery, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            holder.bindGalleryItem(galleryItem);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class PhotoTask extends AsyncTask<String, Void, List<GalleryItem>> {

        @Override
        protected List<GalleryItem> doInBackground(String... params) {
            String query = null;
            List<GalleryItem> galleryItems;
            try {
                if (params != null && params.length > 0)
                    query = params[0];

                if (query == null) {
                    galleryItems = new FlickrFetcher().fetchPopular();
                } else {
                    galleryItems = new FlickrFetcher().searchGalleryItems(query);
                }
                return galleryItems;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            super.onPostExecute(galleryItems);

            mGalleryItems = galleryItems;
            setupAdapter();
        }
    }

}
