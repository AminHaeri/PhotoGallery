package com.example.photogallery.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.example.photogallery.utils.Services;

import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PollJobService extends JobService {

    private static final String TAG = "PollJobService";
    private static final int JOB_ID = 1;

    private PollTask mCurrentTask;

    public static void scheduleService(Context context, boolean isOn) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);

        JobInfo.Builder jobBuilder = new JobInfo.Builder(JOB_ID, new ComponentName(context, PollJobService.class))
//                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobBuilder.setMinimumLatency(TimeUnit.MINUTES.toMillis(1));
        } else {
            jobBuilder.setPeriodic(TimeUnit.MINUTES.toMillis(15));
        }

        JobInfo jobInfo = jobBuilder.build();
        if (!isOn) {
            if (!isScheduled(context))
                jobScheduler.schedule(jobInfo);
        } else {
            jobScheduler.cancel(JOB_ID);
        }
    }

    public static boolean isScheduled(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == JOB_ID) {
                return true;
            }
        }

        return false;
    }

    public PollJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        mCurrentTask = new PollTask();
        mCurrentTask.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mCurrentTask != null) {
            mCurrentTask.cancel(true);
        }

        return false;
    }

    private class PollTask extends AsyncTask<JobParameters, Void, Void> {

        @Override
        protected Void doInBackground(JobParameters... jobParameters) {
            Services.pollServerAndSendNotification(PollJobService.this, TAG);
            jobFinished(jobParameters[0], false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                scheduleService(PollJobService.this, true);
            }

            return null;
        }

    }
}
