package com.example.timsong.drawables;

import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void transitionDrawable(View view) {
        // Transition the clicked view's background drawable:)
        Drawable backgroundDrawable = view.getBackground();
        if (backgroundDrawable instanceof TransitionDrawable) {
            ((TransitionDrawable) backgroundDrawable).startTransition(1000);
        }
    }

    public void progressForward(View view) {
        // Retrieve the reference for my progress view:)
        View progressView = findViewById(R.id.layerDrawableView);
        LayerDrawable background = (LayerDrawable) progressView.getBackground();
        ClipDrawable clipLayer = (ClipDrawable) background.findDrawableByLayerId(R.id.clipDrawableLayer);
        if (clipLayer.getLevel() < 10000) {
            clipLayer.setLevel(clipLayer.getLevel() + 1000);
        } else {
            Toast.makeText(this, "Mission completed already:)", Toast.LENGTH_SHORT).show();
        }
    }
}
