package com.example.timsong.loaderdemo;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.provider.ContactsContract.Contacts;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

/**
 * Created by timsong on 2017/1/5.
 */

public class ContactsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {

    private static final int CONTACTS_LOADER_ID = 0;
    private static final String TAG = "ContactsFragment";
    private static final String[] CONTACTS_SUMMARY_PROJECTION = {
            Contacts._ID,
            Contacts.DISPLAY_NAME,
            Contacts.CONTACT_STATUS,
            Contacts.CONTACT_PRESENCE,
            Contacts.PHOTO_ID,
            Contacts.LOOKUP_KEY};
    SimpleCursorAdapter mCursorAdapter;

    String mQueryText;

    /**
     * When this method is called, the host Activity is ready to start.
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText("No phone numbers"); // Prefer to fetch the text from resources.

        // Need to indicate the host Activity that this Fragment want to add an options menu item.
        setHasOptionsMenu(true);

        mCursorAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, null,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.CONTACT_STATUS},
                new int[]{android.R.id.text1, android.R.id.text2}, 0); // No flag for special CursorAdapter behavior.
        setListAdapter(mCursorAdapter);

        getLoaderManager().initLoader(CONTACTS_LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader loader = null;
        switch (id) {
            case CONTACTS_LOADER_ID:
                Uri baseUri;
                if (TextUtils.isEmpty(mQueryText)) {
                    baseUri = ContactsContract.Contacts.CONTENT_URI;
                } else {
                    baseUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(mQueryText));
                }
                String selection = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                        + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                        + ContactsContract.Contacts.DISPLAY_NAME + " != '' ))";
                loader = new CursorLoader(getActivity(), baseUri,
                        CONTACTS_SUMMARY_PROJECTION, selection, null, Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
                break;
            default:
                Log.w(TAG, "onCreateLoader meets unexpected id: " + id);
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the cursor in to refresh the data. The framework take care of the old cursor.
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Make sure no longer using loader data.
        mCursorAdapter.swapCursor(null);  // Different to the changeCursor() method, which closed the old cursor.
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Place an action bar item for searching contacts.
        MenuItem item = menu.add("Search");
        item.setIcon(android.R.drawable.ic_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView sv = new SearchView(getActivity());
        sv.setOnQueryTextListener(this);
        item.setActionView(sv);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
        /*
        Return true, indicating the query submit has been handled by this listener;
        otherwise, it will be handled by the SearchView with default action.
        */
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Handle this event here. Update the query text, and restart the contacts query loader.
        mQueryText = TextUtils.isEmpty(newText)? null : newText;
        getLoaderManager().restartLoader(CONTACTS_LOADER_ID, null, this);
        return true;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick, POSITION: " + position);
    }
}
