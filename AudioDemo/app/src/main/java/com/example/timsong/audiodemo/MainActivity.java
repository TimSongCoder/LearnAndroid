package com.example.timsong.audiodemo;

import android.content.ContentUris;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.IOException;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.STREAM_MUSIC;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, MediaPlayer.OnPreparedListener, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "MainActivity";
    private CursorAdapter mCursorAdapter;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.list_view_audio);
        listView.setEmptyView(View.inflate(this, R.layout.empty_view_layout, null));
        mCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
                new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media._ID}, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        listView.setAdapter(mCursorAdapter);

        // Retrieve media from local device through MediaStore content provider.
        getSupportLoaderManager().initLoader(0, null, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Play the selected audio.
                long itemId = mCursorAdapter.getItemId(position);
                Log.i(TAG, "onItemClick, AUDIO_ITEM_ID: " + itemId);
                playAudio(itemId);
            }
        });
    }

    private void playAudio(long itemId) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, itemId);
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setAudioStreamType(STREAM_MUSIC);
        }
        try {
            mMediaPlayer.setDataSource(this, contentUri);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(TAG, "RETRIEVE WITH URI FAILED.", e);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG, "onPrepared");
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        am.requestAudioFocus(this, STREAM_MUSIC, AUDIOFOCUS_GAIN);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AUDIOFOCUS_GAIN) {
            mMediaPlayer.start();
        } else {
            Toast.makeText(this, "Can not gain audio focus to play music.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "MediaPlayer.onError: what," + what + "; extra, " + extra);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_add_record:
                showRecordDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showRecordDialog() {
        new AddRecordFragment().show(getSupportFragmentManager(), "AddRecordFragment");
    }
}
