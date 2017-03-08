package com.example.timsong.audiodemo;


import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddRecordFragment extends DialogFragment {

    private static final String TAG = "AddRecordFragment";
    boolean mIsRecording = false;
    String mRecordFilePath = null;

    MediaRecorder mRecorder;
    private MediaScannerConnection mMediaScannerConnection;

    public AddRecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_record, container, false);
        final Button actionButton = (Button) view.findViewById(R.id.button_start_record);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRecording) {
                    stopRecord();
                    actionButton.setText(R.string.start_record);
                } else {
                    startRecord();
                    actionButton.setText(R.string.stop_record);
                }
                mIsRecording = !mIsRecording;
            }
        });
        return view;
    }

    private void stopRecord() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        publishRecord();
    }

    private void publishRecord() {
        mMediaScannerConnection = new MediaScannerConnection(getContext(), new MediaScannerConnection.MediaScannerConnectionClient() {
            @Override
            public void onMediaScannerConnected() {
                Log.i(TAG, "onMediaScannerConnected");
                mMediaScannerConnection.scanFile(mRecordFilePath, "audio/*");
            }

            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.i(TAG, "onScanCompleted: " + path);
                mMediaScannerConnection.disconnect();
            }
        });
        mMediaScannerConnection.connect();
    }

    private void startRecord() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // Record to external cache directory for visibility.
            mRecordFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + System.currentTimeMillis() + ".3gp";
            mRecorder.setOutputFile(mRecordFilePath);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(TAG, "MediaRecorder preparation failed.", e);
            }

            mRecorder.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
        if (mMediaScannerConnection != null && mMediaScannerConnection.isConnected()) {
            mMediaScannerConnection.disconnect();
            mMediaScannerConnection = null;
        }
    }
}
