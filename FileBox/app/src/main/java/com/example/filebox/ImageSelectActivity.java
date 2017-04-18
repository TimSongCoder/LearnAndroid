package com.example.filebox;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;

public class ImageSelectActivity extends AppCompatActivity {

    private static final String TAG = "ImageSelectActivity";
    private String[] filenames;
    public static final String ACTION_SELECT_RESULT = "com.example.filebox.SELECT_RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        File[] files = getDir(MainActivity.THUMBNAIL_DIR, MODE_PRIVATE).listFiles();
        filenames = new String[files.length];
        for(int i=0; i<files.length; i++){
            filenames[i] = files[i].getAbsolutePath();
        }
        ListView listView = (ListView) findViewById(R.id.listView);
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filenames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Share the file to request app.
                Intent resultIntent = new Intent(ACTION_SELECT_RESULT);
                Uri fileUri = null;
                try{
                    fileUri = FileProvider.getUriForFile(ImageSelectActivity.this, "com.example.filebox.fileprovider", new File(filenames[position]));
                }catch (IllegalArgumentException e){
                    Log.e(TAG, "GET URI FOR FILE FAILED.", e);
                }
                if(fileUri != null){
                    resultIntent.setData(fileUri);
                    resultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    setResult(RESULT_OK, resultIntent);
                }
                finish();
            }
        });
    }
}
