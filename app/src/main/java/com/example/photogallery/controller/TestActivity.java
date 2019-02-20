package com.example.photogallery.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.example.photogallery.service.MyService;
import com.example.photogallery.R;

/**
 * This is for testing
 */
public class TestActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mTextView = findViewById(R.id.textView);

        Intent intent = new Intent(this, MyService.class);
        startActivity(intent);


        TestAsync testAsync = new TestAsync();
        testAsync.execute();
    }

    private class TestAsync extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... voids) {
            for (int i = 1; i <= 100; i++) {
                publishProgress(i);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Done!!!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            int i = values[0];
            mTextView.setText(i + "");
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);

            mTextView.setText(string);
        }
    }
}
