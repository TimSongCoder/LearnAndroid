package com.example.timsong.dragdrop;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.res.Resources;
import android.graphics.drawable.ClipDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnDragListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the reference for views who support drag-drop operations.
        View brightBlueView = findViewById(R.id.view_bright_blue);
        View lightGreenView = findViewById(R.id.view_light_green);
        View darkGreenView = findViewById(R.id.view_dark_green);
        View lightBlueView = findViewById(R.id.view_light_blue);

        // Set listeners for drag-drop operation trigger.
        brightBlueView.setOnLongClickListener(this);
        lightBlueView.setOnLongClickListener(this);
        lightGreenView.setOnLongClickListener(this);
        darkGreenView.setOnLongClickListener(this);

        // Set DragEvent listeners for views who are interested about it.
        brightBlueView.setOnDragListener(this);
        lightBlueView.setOnDragListener(this);
        darkGreenView.setOnDragListener(this);
        lightGreenView.setOnDragListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        ClipData clipData = ClipData.newPlainText((String) v.getTag(), (String) v.getTag());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            v.startDragAndDrop(clipData, new View.DragShadowBuilder(v), null, 0);
        } else {
            v.startDrag(clipData, new View.DragShadowBuilder(v), null, 0);
        }
        return true;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Log.i(TAG, "onDrag: DragEvent - action, " + event.getAction() + ", view_tag: " + v.getTag());
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    ((ImageView) v).setColorFilter(android.R.color.holo_orange_light);
                    v.invalidate();
                    return true;
                }
                return false;
            case DragEvent.ACTION_DRAG_ENTERED:
                ((ImageView) v).setColorFilter(android.R.color.holo_orange_dark);
                v.invalidate();
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                // Do nothing here
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                ((ImageView) v).setColorFilter(android.R.color.holo_orange_light);
                v.invalidate();
                return true;
            case DragEvent.ACTION_DROP:
                CharSequence droppedData = event.getClipData().getItemAt(0).getText();
                View rootView = findViewById(android.R.id.content);
                View dragOriginatedView = rootView.findViewWithTag(droppedData);

                ((ImageView) v).setImageDrawable(((ImageView) dragOriginatedView).getDrawable());
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                ((ImageView) v).setColorFilter(null);
                v.invalidate();
                return true;
            default:
                Log.i(TAG, "UNEXPECTED DRAG_EVENT_ACTION: " + event.getAction());
                return false;
        }
    }
}
