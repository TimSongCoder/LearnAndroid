package com.example.volleydemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void requestString(View view) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this); // This convenient method will start the queue automatically.
        final String requestUrl = "https://cn.bing.com";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) { // These response handler is guaranteed to run in main thread.
                // Display the first 500 characters of the response string.
                ((TextView) findViewById(R.id.textView)).setText(response.substring(0, Math.min(1500, response.length())));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Volley always delivers parsed responses on the main thread.
                ((TextView) findViewById(R.id.textView)).setText("Volley Error");
                Log.e(TAG, "onErrorResponse", error);
            }
        });
        // Set a tag for this request. Can be used to cancel all requests with this tag through RequestQueue.cancelAll.
        stringRequest.setTag(TAG);

        // Add the request to RequestQueue.
        queue.add(stringRequest);
    }
}
