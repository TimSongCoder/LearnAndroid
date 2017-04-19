package com.example.nfcbeam;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_READ_EXTERNAL_PERMISSION = 1;
    private static final int REQUEST_NFC_PERMISSION = 2;
    private boolean needInitialization = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.textView);

        // Test External storage readable at least.
        String externalStorageState = Environment.getExternalStorageState();
        Log.d(TAG, "External Storage State: " + externalStorageState);
        if(Environment.MEDIA_MOUNTED.equals(externalStorageState) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState)){
            // Check Permission for EXTERNAL_STORAGE_READ.
            if(PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                // Find out interesting directory, e.g pictures here.
                File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                File[] photos = new File(dcimDir, "Camera").listFiles();
                for(int i=0; i< Math.min(photos.length, 10); i++){
                    Log.d(TAG, "PICTURE: " + photos[i].getAbsolutePath());
                }
            } else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                new AlertDialog.Builder(this).setMessage("I need READ EXTERNAL PERMISSION to work for you:)").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_PERMISSION);
                        dialog.dismiss();
                    }
                }).create().show();
            }else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_PERMISSION);
            }

            // Check NFC feature existence.
            if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)){
                // Check Permission for NFC usage.
                if(PermissionChecker.checkSelfPermission(this, Manifest.permission.NFC) == PackageManager.PERMISSION_GRANTED){
                    // YOU CAN try the Android Beam feature.
                    initializeNfc();
                }else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.NFC)){
                    new AlertDialog.Builder(this).setMessage("I need NFC PERMISSION to TRANSFER file to other device:)").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.NFC}, REQUEST_NFC_PERMISSION);
                            dialog.dismiss();
                        }
                    }).create().show();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.NFC}, REQUEST_NFC_PERMISSION);
                }
            }else{
                Toast.makeText(this, "Your device has no NFC feature support.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeNfc() {
        // NFC enabled or disabled.
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter != null && nfcAdapter.isEnabled()){
            nfcAdapter.setBeamPushUrisCallback(new NfcAdapter.CreateBeamUrisCallback() {
                @Override
                public Uri[] createBeamUris(NfcEvent event) {
                    Log.d(TAG, "createBeamUris IS CALLED.");
                    File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File[] photos = new File(dcimDir, "Camera").listFiles();
                    Uri[] uris = new Uri[Math.min(photos.length, 6)];
                    for(int i=0; i<uris.length; i++){
                        uris[i] = Uri.fromFile(photos[i]);
                    }
                    return uris;
                }
            }, this);

        }else{
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                            needInitialization = true;
                            dialog.dismiss();
                            break;
                        default:
                            dialog.dismiss();
                            break;
                    }
                }
            };
            new AlertDialog.Builder(this).setMessage("NFC is not enabled yet, you need to activate it manually.")
                    .setPositiveButton("Activate", listener)
                    .setNegativeButton("Cancel", listener)
                    .create().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_READ_EXTERNAL_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "READ_EXTERNAL_STORAGE PERMISSION IS GRANTED.");
                    // Find out interesting directory, e.g pictures here.
                    File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File[] photos = new File(dcimDir, "Camera").listFiles();
                    for(int i=0; i< Math.min(photos.length, 10); i++){
                        Log.d(TAG, "PICTURE: " + photos[i].getAbsolutePath());
                    }
                }
                break;
            case REQUEST_NFC_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "NFC PERMISSION IS GRANTED.");
                    initializeNfc();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(needInitialization){
            initializeNfc();
            needInitialization = false;
        }
    }
}
