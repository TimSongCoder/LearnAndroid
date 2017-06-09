/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.example.android.immersivemode;

import android.gesture.Gesture;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.example.android.common.activities.SampleActivityBase;
import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;

import java.util.Locale;

/**
 * A simple launcher activity containing a summary sample description
 * and a few action bar buttons.
 */
public class MainActivity extends SampleActivityBase {

    public static final String TAG = "MainActivity";

    public static final String FRAGTAG = "ImmersiveModeFragment";

    public static final String GESTURE_TAG = "GESTURE_DETECTOR";

    private GestureDetectorCompat mGestureDetector;
    private VelocityTracker mVelocityTracker;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: " + event);
        mGestureDetector.onTouchEvent(event);
        // Let the GestureDetector analyze the given MotionEvent and it will trigger the appropriate callbacks OnGestureListener supplied if applicable.

        // Try the VelocityTracker API out as introduced in training doc.
        int pointerIndex = event.getActionIndex(); // The index if for pointer in this case.
        int actionMask = event.getActionMasked();
        int pointerId = event.getPointerId(pointerIndex); // The pointer id is needed to retrieve pointer associated data.
        switch (actionMask){
            case MotionEvent.ACTION_DOWN:
                if(mVelocityTracker == null){
                    mVelocityTracker = VelocityTracker.obtain();
                }else{
                    // Reset the VelocityTracker back to its initial state.
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);
                // Add a user's movement to the tracker. As the method contract say, you should call this
                // initial ACTION_DOWN, the following ACTION_MOVE events that you receive, and the final
                // ACTION_UP. You can, however, call this for whichever other events you want.
                return true;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000); // Compute velocity in seconds unit. Like 600 pixels per second.
                Log.d(TAG, String.format(Locale.US, "onTouchEvent, X velocity: %1s; Y velocity: %2s",
                        VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId),
                        VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId)));
                return true;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.addMovement(event);
            case MotionEvent.ACTION_CANCEL:
                // Do what the VelocityTracker#obtain() method says. Android happy, you happy.
                mVelocityTracker.recycle(); // Return a VelocityTracker back to be re-used by others. You MUST NOT touch the object after calling this function.
                mVelocityTracker = null; // I have encountered many crashes because of IllegalStateException saying that "Already in the pool!" before I add this line of code.
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        Log.d(TAG, "onGenericMotionEvent: " + event); // This is not by called at all. It handled by views' onTouchEvent.
        return super.onGenericMotionEvent(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportFragmentManager().findFragmentByTag(FRAGTAG) == null ) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ImmersiveModeFragment fragment = new ImmersiveModeFragment();
            transaction.add(fragment, FRAGTAG);
            transaction.commit();
        }

        // Feel the GestureDetector work flow.
        mGestureDetector = new GestureDetectorCompat(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                Log.d(GESTURE_TAG, "OnGestureListener#onDown: ");
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                Log.d(GESTURE_TAG, "OnGestureListener#onShowPress: ");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(GESTURE_TAG, "OnGestureListener#onSingleTapUp: ");
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d(GESTURE_TAG, "OnGestureListener#onScroll: ");
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(GESTURE_TAG, "OnGestureListener#onLongPress: ");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(GESTURE_TAG, "OnGestureListener#onFling: ");
                return false;
            }
        });

        mGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d(GESTURE_TAG, "OnDoubleTapListener#onSingleTapConfirmed: ");
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d(GESTURE_TAG, "OnDoubleTapListener#onDoubleTap: ");
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                Log.d(GESTURE_TAG, "OnDoubleTapListener#onDoubleTapEvent: ");
                return false;
            }
        });

        findViewById(R.id.sample_output).setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                Log.d(TAG, "OnGenericMotionListener#onGenericMotion: ");
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /** Create a chain of targets that will receive log data */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());
        logFragment.getLogView().setTextAppearance(this, R.style.Log);
        logFragment.getLogView().setBackgroundColor(Color.WHITE);


        Log.i(TAG, "Ready");
    }
}
