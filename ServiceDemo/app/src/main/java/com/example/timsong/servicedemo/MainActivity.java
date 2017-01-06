package com.example.timsong.servicedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int mIntentServiceRequestCount;
    private TextView mIntentServiceRequestCounterView;

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
}
