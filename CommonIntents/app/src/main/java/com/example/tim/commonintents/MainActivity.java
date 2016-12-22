package com.example.tim.commonintents;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_create_alarm:
                createAlarm("下班买菜", 10, 40);
                break;
            case R.id.button_create_timer:
                startTimer("下班倒计时", 60);
                break;
        }
    }

    public void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);  // Vendor like Xiaomi support alarm create ui skip too.
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            if(BuildConfig.DEBUG){
                Log.d(TAG, "createAlarmIntent resolved.");
            }
        }
    }

    public void startTimer(String message, int seconds) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        // The ui skip support implementation varies from vendor to vendor, may be no visual tip for timer while it succeed like Xiaomi's device does.
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            if(BuildConfig.DEBUG){
                Log.d(TAG, "startTimerIntent resolved.");
            }
        }
    }
}
