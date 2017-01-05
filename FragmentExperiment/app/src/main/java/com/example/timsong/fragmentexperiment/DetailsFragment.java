package com.example.timsong.fragmentexperiment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by timsong on 2017/1/4.
 */

public class DetailsFragment extends Fragment {
    private static final String PLAY_INDEX = "selected_play_index";

    /**
     * Create a new instance of DetailsFragment, initialized to show the text for
     * the specified selectedPlayIndex.
     * @param selectedPlayIndex the index of the selected play.
     * @return a new instance of this Fragment.
     */
    public static DetailsFragment newInstance(int selectedPlayIndex) {
        DetailsFragment fragment = new DetailsFragment();

        Bundle args = new Bundle();
        args.putInt(PLAY_INDEX, selectedPlayIndex);
        fragment.setArguments(args);

        return fragment;
    }

    public int getShownIndex(){
        return getArguments().getInt(PLAY_INDEX, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScrollView scrollView = new ScrollView(getActivity());
        TextView textView = new TextView(getActivity());
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getActivity().getResources().getDisplayMetrics());
        textView.setPadding(padding, padding, padding, padding);
        scrollView.addView(textView);

        textView.setText(Shakespeare.DIALOGUE[getShownIndex()]);

        return scrollView;
    }
}
