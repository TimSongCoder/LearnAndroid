package com.example.timsong.menus;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

public class MainActivity extends ListActivity {

    ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
            getActionBar().show();
        }
        final ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.contextual_action_mode_menu, menu);
                mActionMode = mode;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_save:
                        showPopupMenu(findViewById(R.id.item_save));
                        return true;
                    case R.id.item_like:
                    case R.id.item_dislike:
                        Toast.makeText(MainActivity.this, "action: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                Toast.makeText(MainActivity.this, mode + " is destroyed.", Toast.LENGTH_SHORT).show();
                mActionMode = null;
            }
        });
        final ArrayAdapter<CharSequence> listAdapter = ArrayAdapter.createFromResource(this,
                R.array.destination_country, android.R.layout.simple_list_item_multiple_choice);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.setItemChecked(position, listView.getCheckedItemPosition() != position);
            }
        });

        listView.setAdapter(listAdapter);
    }

    private void showPopupMenu(View actionView) {
        PopupMenu popupMenu = new PopupMenu(this, actionView);
        popupMenu.inflate(R.menu.save_popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_save_internal:
                    case R.id.item_save_sd_card:
                        Toast.makeText(MainActivity.this, "action: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        if (mActionMode != null) {
                            mActionMode.finish();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
}
