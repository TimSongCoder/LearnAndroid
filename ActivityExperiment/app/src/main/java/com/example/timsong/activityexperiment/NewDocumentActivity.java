package com.example.timsong.activityexperiment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class NewDocumentActivity extends AppCompatActivity {

    public static final String TAG = "NewDocumentAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate is called.");
        setContentView(R.layout.activity_new_document);
        setDocumentCounterText(String.valueOf(getIntent().getIntExtra(MainActivity.DOCUMENT_COUNT, 0)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume is called.");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setDocumentCounterText("\nReusing the document activity.");
        Log.d(TAG, "onNewIntent is called.");
    }

    private void setDocumentCounterText(String text) {
        ((TextView) findViewById(R.id.text_view_document_counter))
                .append(text);
    }

    public void startThirdActivity(View view) {
        startActivity(new Intent(this, ThirdActivity.class));
    }
}
