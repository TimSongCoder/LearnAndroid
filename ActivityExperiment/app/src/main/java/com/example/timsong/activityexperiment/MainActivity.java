package com.example.timsong.activityexperiment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView mTextViewLifecycleRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewLifecycleRecord = (TextView) findViewById(R.id.text_view_lifecycle_callback_record);
        onActivityStateChanged(Thread.currentThread().getStackTrace()[2].getMethodName() + " is called.");
    }

    @Override
    protected void onStart() {
        super.onStart();
        onActivityStateChanged(Thread.currentThread().getStackTrace()[2].getMethodName() + " is called.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        onActivityStateChanged(Thread.currentThread().getStackTrace()[2].getMethodName() + " is called.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        onActivityStateChanged(Thread.currentThread().getStackTrace()[2].getMethodName() + " is called.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        onActivityStateChanged(Thread.currentThread().getStackTrace()[2].getMethodName() + " is called.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onActivityStateChanged(Thread.currentThread().getStackTrace()[2].getMethodName() + " is called." + "\n");
    }

    // In addition to the 6 core set of callbacks overridden above, onRestart() is important to understand too.
    @Override
    protected void onRestart() {
        super.onRestart();
        onActivityStateChanged(Thread.currentThread().getStackTrace()[2].getMethodName() + " is called.");
    }

    private void onActivityStateChanged(String recordMsg) {
        Log.d(TAG, recordMsg);
        // Append the recordMsg to the record TextView
        mTextViewLifecycleRecord.append(recordMsg + "\n");
    }
}
