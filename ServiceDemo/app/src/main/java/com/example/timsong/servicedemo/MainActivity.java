package com.example.timsong.servicedemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int mIntentServiceRequestCount;
    private TextView mIntentServiceRequestCounterView;
    private ServiceConnection mRandomNumServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            mRandomNumService = (LocalService) binder.getService();
            isRandomNumServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isRandomNumServiceBound = false;
        }
    };
    private LocalService mRandomNumService;
    private boolean isRandomNumServiceBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startIntentService(View view) {
        // Start the ExampleIntentService, requesting for a service.
        startService(new Intent(this, ExampleIntentService.class));
        updateIntentServiceRequestCounter();
    }

    private void updateIntentServiceRequestCounter() {
        if (mIntentServiceRequestCounterView == null) {
            mIntentServiceRequestCounterView = (TextView) findViewById(R.id.text_view_intent_service_request_count);
        }
        mIntentServiceRequestCounterView.setText(String.valueOf(++mIntentServiceRequestCount));
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, LocalService.class), mRandomNumServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isRandomNumServiceBound){
            unbindService(mRandomNumServiceConnection);
            isRandomNumServiceBound = false;
        }
    }

    public void getRandomNumFromLocalService(View view) {
        if(isRandomNumServiceBound){
            int num = mRandomNumService.getRandomNum();
            ((TextView)findViewById(R.id.text_view_random_num)).setText(String.valueOf(num));
        }
    }
}
