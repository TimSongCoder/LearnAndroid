package com.example.timsong.activityexperiment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String STATE_KILL_FLAG = "app_kill_flag";
    private static final String STATE_KILL_TIME = "app_kill_time";
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
        // This method is not always invoked, e.g. when I kill the app through the Application Manager on Xiaomi Phone.
        // While on the same device, it is invoked when I end the app through clear it out in the recent apps list.
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

    /*
    As my exploration on Xiaomi device, when your activity is overlapped by another one, this method is called
    between onPause() method and onStop() method; when your activity is closed intentionally by user(back pressed, kill the app actively etc.)
     this method is not called.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        onActivityStateChanged(Thread.currentThread().getStackTrace()[2].getMethodName() + " is called.");
        outState.putBoolean(STATE_KILL_FLAG, true);
        outState.putString(STATE_KILL_TIME, new Date().toString());
        super.onSaveInstanceState(outState);
    }

    /*
    This method may not be called even the corresponding onSaveInstanceState() method has been called for many times.
    This method is designed to handle the unusual death of the activity.
     */
    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onActivityStateChanged(Thread.currentThread().getStackTrace()[2].getMethodName() + " is called.");

        CheckBox checkedBox = (CheckBox) findViewById(R.id.checked_tv_restore);
        checkedBox.append("\nTime being killed: " + savedInstanceState.getString(STATE_KILL_TIME));
        checkedBox.setChecked(savedInstanceState.getBoolean(STATE_KILL_FLAG));
    }
}
