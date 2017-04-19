package com.example.filebox;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE_THUMBNAIL = 1;
    public static final String THUMBNAIL_DIR = "thumbnails";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void generateFile(View view){
        // For demonstration purpose, take a photo using Intent, saving the thumbnail as file.
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE_THUMBNAIL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_IMAGE_CAPTURE_THUMBNAIL:
                if(resultCode == RESULT_OK){
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    if(thumbnail!=null){
                        File thumbnailDir = new File(getFilesDir(), THUMBNAIL_DIR);
                        if(!thumbnailDir.exists()){
                            thumbnailDir.mkdirs();
                        }
                        try {
                            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(new File(thumbnailDir, System.currentTimeMillis() + ".png")));
                            Toast.makeText(this, "Thumbnail is saved successfully.", Toast.LENGTH_SHORT).show();
                        } catch (FileNotFoundException e) {
                            Log.e(TAG, "CAN NOT SAVE THUMBNAIL.", e);
                        }
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
