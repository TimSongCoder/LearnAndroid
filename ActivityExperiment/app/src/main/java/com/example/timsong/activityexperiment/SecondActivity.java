package com.example.timsong.activityexperiment;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SecondActivity", "onDestroy is called.");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("SecondActivity", "onRestoreInstanceState is called.");
        ((TextView)findViewById(R.id.textView)).append("\nKilled time: " + savedInstanceState.getString(MainActivity.STATE_KILL_TIME));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("SecondActivity", "onSaveInstanceState is called.");
        outState.putString(MainActivity.STATE_KILL_TIME, new Date().toString());
        super.onSaveInstanceState(outState);
    }

}
