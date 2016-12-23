package com.example.tim.commonintents;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_SELECT_CONTACT = 1;
    private static final int REQUEST_SELECT_CONTACT_PHONE = 2;
    private static final int REQUEST_INSERT_CONTACT = 3;
    private static final int REQUEST_SELECT_FILE = 4;
    private static final int REQUEST_OPEN_FILE = 5;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK) {
            final Uri contactUri = data.getData();
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onActivityResult - SelectedContact's URI: " + contactUri.toString());
            }
            DialogInterface.OnClickListener dialogOnclickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = null;
                    switch (i) {
                        case DialogInterface.BUTTON_NEUTRAL:
                            intent = new Intent(Intent.ACTION_VIEW, contactUri);
                            break;
                        case DialogInterface.BUTTON_POSITIVE:
                            intent = new Intent(Intent.ACTION_EDIT, contactUri);
                            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, "saodiseng@shaolin.com"); // add an email address.
                            break;
                    }
                    if (intent != null && intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    dialogInterface.dismiss();
                }
            };
            new AlertDialog.Builder(this).setTitle("Got a contact")
                    .setMessage("What do you want to do with the selected contact?")
                    .setNeutralButton("View The Contact", dialogOnclickListener) // view the contact's detail information
                    .setPositiveButton("Edit the contact", dialogOnclickListener)
                    .create().show();
        } else if (requestCode == REQUEST_SELECT_CONTACT_PHONE && resultCode == RESULT_OK) {
            Uri phoneUri = data.getData();
            // retrieve the phone value for use
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getContentResolver().query(phoneUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String phoneStr = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onActivityResult - SelectedContactPhone: " + phoneStr);
                    Toast.makeText(this, "phone: " + phoneStr, Toast.LENGTH_LONG).show();
                }
                cursor.close();
            }
        } else if (requestCode == REQUEST_INSERT_CONTACT && resultCode == RESULT_OK) {
            Toast.makeText(this, "Contact inserted successfully.", Toast.LENGTH_LONG).show();
            // No result back, result callback done by intent handler component already.
        } else if (requestCode == REQUEST_SELECT_FILE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            // full-size image file, with temporary access permission with the host activity's lifecycle, need import the copy if you want to access it later
            // retrieve the thumbnail for use
            Bitmap imageThumbnail = data.getParcelableExtra("data");
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onActivityResult - selected a photo file: " + imageUri + ", with thumbnail: " + (imageThumbnail != null));  // Unfortunately, it is null.
            }
        }else if(requestCode == REQUEST_OPEN_FILE && resultCode == RESULT_OK){
            // single file, open file uri stored in data; while multiple files stored in clipdata
            Uri fileOpenUri = data.getData();  // long term access permission uri
            if(BuildConfig.DEBUG){
                Log.d(TAG, "onActivityResult - OPEN FILE: " + fileOpenUri);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            findViewById(R.id.button_create_timer).setEnabled(false);
            findViewById(R.id.button_show_alarms).setEnabled(false);
            findViewById(R.id.button_open_file).setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_create_alarm:
                createAlarm("下班买菜", 10, 40);
                break;
            case R.id.button_create_timer:
                startTimer("下班倒计时", 60);
                break;
            case R.id.button_show_alarms:
                showAllAlarms();
                break;
            case R.id.button_add_calendar_event:
                long beginTime = System.currentTimeMillis() + 10 * 60 * 1000;// 10 minutes later
                long endTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000; // a day later
                addCalendarEvent("TestCalendarEventAdd", null, beginTime, endTime);
                break;
            case R.id.button_start_camera_in_still_image_mode:
                startCameraInStillImageMode();
                break;
            case R.id.button_start_camera_in_video_mode:
                startCameraInVideoMode();
                break;
            case R.id.button_select_contact:
                selectContact();
                break;
            case R.id.button_select_specific_contact_data:
                selectContactPhone();
                break;
            case R.id.button_insert_contact:
                insertContact("风清扬", "fengqingyang@huashan.com");
                break;
            case R.id.button_compose_email:
                composeEmail("2016华山论剑", new String[]{"fengqingyang@huashan.com", "saodiseng@shaolin.com"});
                break;
            case R.id.button_select_file:
                selectFile();
                break;
            case R.id.button_open_file:
                openFile();
                break;
        }
    }

    private void openFile() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            // only the system will respond to this intent, no need to verify resolution.
            startActivityForResult(intent, REQUEST_OPEN_FILE);
        }
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_FILE);
        }
    }

    private void composeEmail(String subject, String[] recipients) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);  // Email app do not respond to ACTION_SEND on Xiaomi device.
        intent.setData(Uri.parse("mailto:"));  // need component who can handle mailto: scheme, aka email app.
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void insertContact(String name, String email) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_INSERT_CONTACT);
        }
    }

    private void selectContactPhone() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_CONTACT_PHONE);
        }
    }

    private void selectContact() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_CONTACT);
        }
    }

    private void startCameraInVideoMode() {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void startCameraInStillImageMode() {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void addCalendarEvent(String title, String location, long begin, long end) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showAllAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    public void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);  // Vendor like Xiaomi support alarm create ui skip too.
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "createAlarmIntent resolved.");
            }
        }
    }

    public void startTimer(String message, int seconds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                    .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                    .putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                    .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            // The ui skip support implementation varies from vendor to vendor, may be no visual tip for timer while it succeed like Xiaomi's device does.
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "startTimerIntent resolved.");
                }
            }
        }
    }
}
