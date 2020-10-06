package msmartds.in.location;


import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;


public class LocationJobIntent extends JobIntentService {

    public static void start(Context context) {
        Intent starter = new Intent(context, LocationJobIntent.class);
        LocationJobIntent.enqueueWork(context, starter);
    }


    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    private static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, LocationJobIntent.class, JOB_ID, intent);
    }

    public static void stopService() {
        //do work

    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        //do work
        intent = new Intent(LocationJobIntent.this, GoogleService.class);
        startService(intent);


    }


}
