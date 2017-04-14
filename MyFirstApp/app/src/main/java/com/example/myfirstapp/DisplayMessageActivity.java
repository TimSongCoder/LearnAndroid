package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * When you create this Activity file through AndroidStudio new > Activity mechanism,
 * the AndroidStudio does three things for you under the hood:
 * 1.Creates the Java file for newly configured Activity;
 * 2.Creates the corresponding layout file for the newly created Activity;
 * 3.Add the required <activity> element in AndroidManifest.xml for the newly created Activity.
 */
public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        // Get the Intent that started this Activity instance and extract the message string.
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        // Capture the TextView to display message string.
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(message);
    }
}
