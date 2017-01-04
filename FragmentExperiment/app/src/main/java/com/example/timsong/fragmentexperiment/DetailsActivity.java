package com.example.timsong.fragmentexperiment;

import android.content.res.Configuration;
import android.support.v4.content.res.ConfigurationHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailsActivity extends AppCompatActivity {

    public static final String Detail_INDEX = "detail_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            // Finish this activity instance, display in dual-pane mode instead. User maybe have rotated the device landscape after this activity started.
            finish();
            return;
        }

        if(savedInstanceState == null){
            // Initialize and plug in the DetailsFragment.
            DetailsFragment detailsFragment = DetailsFragment.newInstance(getIntent().getIntExtra(Detail_INDEX, 0));
            getFragmentManager().beginTransaction().add(android.R.id.content, detailsFragment).commit();
        }
    }
}
