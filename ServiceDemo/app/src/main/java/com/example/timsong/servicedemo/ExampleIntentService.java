package com.example.timsong.servicedemo;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * IntentService will launch a worker thread and stop the service at appropriate
 * time all by itself.
 */
public class ExampleIntentService extends IntentService {

    private static final String TAG = "ExampleIntentService";

    public ExampleIntentService() {
        super("ExampleIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Serving a request with intent: " + intent);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt(); // Restore the interrupted state.
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy is called.");
    }
}
