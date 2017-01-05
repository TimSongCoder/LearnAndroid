package com.example.timsong.fragmentexperiment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
    private static final String TAG = "DetailsFragment";

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
        Log.d(TAG, "onCreateView is called.");
        ScrollView scrollView = new ScrollView(getActivity());
        TextView textView = new TextView(getActivity());
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getActivity().getResources().getDisplayMetrics());
        textView.setPadding(padding, padding, padding, padding);
        scrollView.addView(textView);

        textView.setText(Shakespeare.DIALOGUE[getShownIndex()]);

        return scrollView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach is called.");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate is called.");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated is called.");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "onViewStateRestored is called.");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart is called.");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume is called.");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause is called.");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState is called.");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop is called.");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView is called.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy is called.");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach is called.");
    }
}
