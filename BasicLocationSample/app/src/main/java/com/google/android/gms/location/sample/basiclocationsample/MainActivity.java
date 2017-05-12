/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.basiclocationsample;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Location sample.
 * <p>
 * Demonstrates use of the Location API to retrieve the last known location for a device.
 * This sample uses Google Play services (GoogleApiClient) but does not need to authenticate a user.
 * See https://github.com/googlesamples/android-google-accounts/tree/master/QuickStart if you are
 * also using APIs that need authentication.
 */
public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    protected static final String TAG = "MainActivity";
    private static final int REQUEST_FIX_FAILURE = 1;
    private static final int REQUEST_COARSE_LOCATION = 2;
    private static final int REQUEST_SETTINGS_RESOLUTION = 3;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    private TextView mTimestampText;
    private String mTimestampLabel;
    private boolean mLocationUpdateFlag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mTimestampLabel = getResources().getString(R.string.timestamp_label);
        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
        mLongitudeText = (TextView) findViewById((R.id.longitude_text));
        mTimestampText = (TextView) findViewById(R.id.timestamp_text);

        buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    // Get location info.
                    getFusedLocation(false);
                else
                    Toast.makeText(this, "Location permission is denied.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Google Service Api is connected.");
        }
    }

    private void getFusedLocation(boolean needUpdate) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Need to explain the reason/rationale of the permission.
                DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
                                break;
                        }
                    }
                };
                new AlertDialog.Builder(this).setMessage("Permission needed to get location of your current device:)")
                        .setPositiveButton("Understand", dialogListener)
                        .setNegativeButton("Not need it", dialogListener)
                        .create().show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
            }
        } else {
            if (needUpdate) {
                final LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_NO_POWER).setFastestInterval(5000);
                // Check the location service settings.
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        new LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                                .build()).setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "checkLocationSettings - ResultCallback - onResult: " + locationSettingsResult.getStatus());
                            switch (locationSettingsResult.getStatus().getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // Current settings are adequate for use, start the location update request.
                                    //noinspection MissingPermission
                                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, MainActivity.this).setResultCallback(new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(@NonNull Status status) {
                                            if (BuildConfig.DEBUG) {
                                                Log.i(TAG, "RequestLocationUpdates - PendingResult - ResultCallback - Status: " + status);
                                            }
                                        }
                                    });
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Current location settings is not ready to use, need change.
                                    try {
                                        locationSettingsResult.getStatus().startResolutionForResult(MainActivity.this, REQUEST_SETTINGS_RESOLUTION);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(TAG, "LOCATION SETTINGS START RESOLUTION FOR RESULT FAILED.", e);
                                    }
                                    break;
                            }
                        }
                    }
                });

            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                updateLocationInfo();
            } else {
                Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
                // The Emulator has no last known location to report:)
            }
        }
    }

    private void updateLocationInfo() {
        mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,
                mLastLocation.getLatitude()));
        mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,
                mLastLocation.getLongitude()));
        mTimestampText.setText(String.format("%1s: %2s", mTimestampLabel, new SimpleDateFormat("HH:mm:ss:SSS, yyyy-MM-dd", Locale.getDefault()).format(new Date(mLastLocation.getTime()))));
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, REQUEST_FIX_FAILURE, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "GooglePlayService error dialog is canceled.");
                }
            }
        }).show();
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    public void getLastLocation(View view) {
        getFusedLocation(false);
    }

    public void getUpdatableLocation(View view) {
        getFusedLocation(true);
        mLocationUpdateFlag = true;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onLocationChanged");
            if (location != null) {
                mLastLocation = location;
                updateLocationInfo();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLocationUpdateFlag && mGoogleApiClient.isConnected()) {
            // Continue to update loaction info.
            getFusedLocation(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTINGS_RESOLUTION) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "REQUEST LOCATION SETTINGS RESOLUTION RESULT BACK: OK? " + (resultCode == RESULT_OK));
            }
        }
    }
}
