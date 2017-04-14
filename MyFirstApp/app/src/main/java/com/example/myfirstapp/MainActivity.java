package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    /* It's a good practice to define keys for intent extras using your app's package name as prefix.
       This ensures the keys are unique, in case your app interacts other apps.
     */
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Called when the user taps the Send button.
     * Take note of the details in this method that are required in order for
     * the system to recognize it as compatible with the android:onClick attribute.
     * 1.Public access; 2.A void return value; 3.A View as the only parameter.
     * @param view
     */
    public void sendMessage(View view){
        /*An Intent is an object that provides runtime binding between separate components, such as two activities.*/
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
