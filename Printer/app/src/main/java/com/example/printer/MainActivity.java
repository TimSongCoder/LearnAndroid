package com.example.printer;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_PRINT_FILE = 1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void pickPrintFile(View view){
        Log.d(TAG, "pickPrintFile IS CALLED.");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_PICK_PRINT_FILE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_PICK_PRINT_FILE:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    String scheme = uri.getScheme();
                    Log.d(TAG, "Picked file URI scheme: " + scheme);
                    if("file".equals(scheme)){
                        try {
                            new PrintHelper(this).printBitmap("Print with File Uri", uri);
                        } catch (FileNotFoundException e) {
                            Log.e(TAG, "PRINT BITMAP FAILED BECAUSE OF FILE NOT FOUND.", e);
                        }
                    }else if("content".equals(scheme)){
                        String type = getContentResolver().getType(uri);
                        Log.d(TAG, "Picked file URI MIME type: " + type);
                        boolean printable = PrintHelper.systemSupportsPrint();
                        Log.d(TAG, "Print is supported: " + printable);
                        if (type != null) {
                            switch (type){
                                case "image/jpeg":
                                case "image/png":
                                case "image/*":
                                    if(printable){
                                        try {
                                            new PrintHelper(this).printBitmap("Image Print Test", uri);
                                        } catch (FileNotFoundException e) {
                                            Log.e(TAG, "PRINT BITMAP FAILED BECAUSE OF FILE NOT FOUND.", e);
                                        }
                                    }
                                    break;
                            }
                        }
                    }

                }else{
                    Toast.makeText(this, "Pick file canceled.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
