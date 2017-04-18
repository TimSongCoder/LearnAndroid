package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    /* It's a good practice to define keys for intent extras using your app's package name as prefix.
       This ensures the keys are unique, in case your app interacts other apps.
     */
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    public static final int REQUEST_MSG_COUNT = 1;
    private static final String TAG = "MainActivity";
    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged");
                // Update the share content now.
                setShareContent(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "onTextChanged");
            }
        });
    }

    private void setShareContent(CharSequence s) {
        if(shareActionProvider != null){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, s.toString());
            shareActionProvider.setShareIntent(intent);
            Log.d(TAG, "UPDATE SHARE INTENT: " + s);
        }else{
            Log.d(TAG, "ShareActionProvider can not be null to function properly.");
        }
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
        startActivityForResult(intent, REQUEST_MSG_COUNT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_MSG_COUNT){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Result OK", Toast.LENGTH_SHORT).show();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Result canceled", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Text Count: " + resultCode, Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        return true;
    }
}
