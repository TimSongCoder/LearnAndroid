package com.example.tim.commonintents;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.timsong.servicedemo.IRemoteAidlInterface;
import com.example.timsong.servicedemo.RemoteAidlCallback;
import com.google.android.gms.actions.NoteIntents;
import com.google.android.gms.actions.ReserveIntents;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_SELECT_CONTACT = 1;
    private static final int REQUEST_SELECT_CONTACT_PHONE = 2;
    private static final int REQUEST_INSERT_CONTACT = 3;
    private static final int REQUEST_SELECT_FILE = 4;
    private static final int REQUEST_OPEN_FILE = 5;
    private static final int REQUEST_GOOGLE_PLAY_SERVICE_ERROR_FIX = 6;
    private static final int REQUEST_CREATE_DOCUMENT = 7;
    private static final int REQUEST_EDIT_DOCUMENT = 8;
    private boolean isDonateServiceBound;
    private DonateIncomingMsgHandler mDonateIncomingMsgHandler;
    private ServiceConnection mDonateServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected is called.");
            mDonateServiceOutgoingMessenger = new Messenger(service);

            isDonateServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected is called.");
            isDonateServiceBound = false;
        }
    };

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
        } else if (requestCode == REQUEST_OPEN_FILE && resultCode == RESULT_OK) {
            // single file, open file uri stored in data; while multiple files stored in clipdata
            Uri fileOpenUri = data.getData();  // long term access permission uri
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onActivityResult - OPEN FILE: " + fileOpenUri);
            }
            dumpFileMetadata(fileOpenUri);
            showImageFile(fileOpenUri);
        } else if (requestCode == REQUEST_CREATE_DOCUMENT && resultCode == RESULT_OK) {
            Toast.makeText(this, "Create Doc Successfully, " + data.getData(), Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_EDIT_DOCUMENT && resultCode == RESULT_OK) {
            ParcelFileDescriptor pfd = null;
            FileOutputStream fileOutputStream = null;
            try {
                pfd = getContentResolver().
                        openFileDescriptor(data.getData(), "w");
                fileOutputStream =
                        new FileOutputStream(pfd.getFileDescriptor());
                fileOutputStream.write(("Overwritten by Tim at " +
                        System.currentTimeMillis() + "\n").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Let the document provider know you're done by closing the stream.
                if (fileOutputStream != null)
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (pfd != null) {
                    try {
                        pfd.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private void showImageFile(Uri fileOpenUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ImageView imageView = new ImageView(this);
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = getContentResolver().openFileDescriptor(fileOpenUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            // TODO Should fetch the file content in background thread, e.g use AsyncTask.
            imageView.setImageBitmap(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                parcelFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        builder.setView(imageView);
        builder.create().show(); // Succeeded.
    }

    private void dumpFileMetadata(Uri fileOpenUri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Cursor cursor = getContentResolver().query(fileOpenUri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    String size = null;
                    if (!cursor.isNull(sizeIndex)) {
                        size = cursor.getString(sizeIndex);
                    }
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "FILE DISPLAY NAME: " + displayName + ", SIZE: " + size);
                    }
                    Toast.makeText(this, "FILE DISPLAY NAME: " + displayName + ", \nSIZE: " + size, Toast.LENGTH_SHORT).show();
                }
            } finally {
                cursor.close();
            }
        } else {
            // Resources released automatically. New feature from Java 7.
            try (Cursor cursor = getContentResolver().query(fileOpenUri, null, null, null, null)) {
                if (cursor != null && cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    String size = null;
                    if (!cursor.isNull(sizeIndex)) {
                        size = cursor.getString(sizeIndex);
                    }
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "FILE DISPLAY NAME: " + displayName + ", SIZE: " + size);
                    }
                    Toast.makeText(this, "FILE DISPLAY NAME: " + displayName + ", \nSIZE: " + size, Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
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
                openImageFile();
                break;
            case R.id.button_reserve_car:
                reserveCar();  // local action
                break;
            case R.id.button_view_location:
                showLocationOnMap();
                break;
            case R.id.button_play_audio_file:
                playAudioFile();
                break;
            case R.id.button_play_media_from_search:
                playMusicFromSearch("Michael Jackson"); // search for artist name
                break;
            case R.id.button_create_note:
                createNote("Use iMac as novice", "Reinstall the macOS, expecting to solve the slow responding problem.");
                break;
            case R.id.button_dial_phone:
                dialPhone("02782330001"); // a fake phone number
                break;
            case R.id.button_perform_web_search:
                searchOnWeb("Michael Jackson");
                break;
            case R.id.button_start_settings:
                startWifiSettings();
                break;
            case R.id.button_send_sms:
                sendSms("13412345678", "Hello, I am still reinstalling the macOS. I can handle it:)");
                break;
            case R.id.button_load_web_page:
                loadWebPage("http://cn.bing.com");
                break;
            case R.id.button_start_activity_experiment:
                Intent intent = new Intent("com.example.timsong.action.ACTIVITY_EXPERIMENT");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.d(TAG, "activityExperiment: NO APP CAN HANDLE THIS ON YOUR DEVICE:)");
                    Toast.makeText(this, "NO APP CAN HANDLE THIS ON YOUR DEVICE:)", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_start_activity_with_flag:
                startActivityWithFlag(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            case R.id.button_start_task_reparent_activity:
                startTaskReparentActivity("com.example.timsong.action.THIRD_ACTIVITY");
                break;
            case R.id.button_donate:
                donate(5);
                break;
            case R.id.button_use_aidl_service:
                useAidlService();
                break;
            case R.id.button_create_document:
                createDocument("text/plain", "MacNote");
                break;
            case R.id.button_edit_document:
                editDocument();
                break;
        }
    }

    private void editDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent, REQUEST_EDIT_DOCUMENT);
    }

    private void createDocument(String mimeType, String docTitle) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, docTitle);
        intent.putExtra(Intent.EXTRA_TEXT, "Mac behaves better than Windows in some aspects.\nI want to try iphone out for some days.");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CREATE_DOCUMENT);
        } else {
            Log.d(TAG, "createDocument: NO APP CAN HANDLE THIS ON YOUR DEVICE:)");
        }
    }

    private void useAidlService() {
        // Bind to a remote service.
        Intent intent = new Intent();
        intent.setClassName("com.example.timsong.servicedemo", "com.example.timsong.servicedemo.RemoteAidlService");
        ServiceConnection aidlServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // Cast and get the reference of the remote aidl interface.
                IRemoteAidlInterface remoteAidlInterface = IRemoteAidlInterface.Stub.asInterface(service);
                // Use the function defined in remote aidl interface.
                try {
                    remoteAidlInterface.registerServiceDestroyCallback(new RemoteAidlCallback.Stub() {
                        @Override
                        public void onServiceDestroyed() throws RemoteException {
                            Log.d(TAG, "RemoteAidlCallback.onServiceDestroyed is called, " + Thread.currentThread());
                        }
                    });
                    Toast.makeText(getApplicationContext(), "RemoteAidlServicePid: " + remoteAidlInterface.getPid(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "RemoteAidlService: pid= " + remoteAidlInterface.getPid());
                } catch (RemoteException e) {
                    e.printStackTrace();
                } finally {
                    // Unbind the remote service, observe the remote service's life state.
                    unbindService(this);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected: " + name);
            }
        };
        bindService(intent, aidlServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Donates mocking for demo use.
     * Used to explore the inter-process communication through Messenger API.
     */
    private void donate(int money) {
        if (isDonateServiceBound) {
            Message message = Message.obtain();
            message.what = 2; // Value need to be exactly corresponding with donate service.
            message.arg1 = money;
            if (mDonateIncomingMsgHandler == null) {
                mDonateIncomingMsgHandler = new DonateIncomingMsgHandler();
                mDonateServiceIncomingMessenger = new Messenger(mDonateIncomingMsgHandler);
            }
            message.replyTo = mDonateServiceIncomingMessenger;
            try {
                mDonateServiceOutgoingMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Service is not bound.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent donateServiceIntent = new Intent();
        donateServiceIntent.setClassName("com.example.timsong.servicedemo", "com.example.timsong.servicedemo.MessengerService");
        // The class name need be fully qualified. The package name parameter was just enough to specify the intended application.
        boolean mBindResult = bindService(donateServiceIntent, mDonateServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "BIND DONATE SERVICE RESULT: " + mBindResult);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isDonateServiceBound) {
            unbindService(mDonateServiceConnection);
            isDonateServiceBound = false;
            mDonateIncomingMsgHandler = null;
            mDonateServiceIncomingMessenger = null;
        }
    }

    private Messenger mDonateServiceOutgoingMessenger, mDonateServiceIncomingMessenger;

    public class DonateIncomingMsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    // Donate successfully as per donate service's reply.
                    Toast.makeText(getApplicationContext(), "Congratulations, donation succeeded.", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }

    private void startTaskReparentActivity(String action) {
        Intent intent = new Intent(action);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "startTaskReparentActivity: NO APP CAN HANDLE THIS ON YOUR DEVICE:)");
            Toast.makeText(this, "NO APP CAN HANDLE THIS ON YOUR DEVICE:)", Toast.LENGTH_LONG).show();
        }
    }

    private void startActivityWithFlag(int intentFlag) {
        Intent intent = new Intent("com.example.timsong.action.SECOND_ACTIVITY");
        intent.setFlags(intentFlag);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "startActivityWithFlag: NO APP CAN HANDLE THIS ON YOUR DEVICE:)");
            Toast.makeText(this, "NO APP CAN HANDLE THIS ON YOUR DEVICE:)", Toast.LENGTH_LONG).show();
        }
    }

    private void loadWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "loadWebPage: NO APP CAN HANDLE THIS ON YOUR DEVICE:)");
            Toast.makeText(this, "NO APP CAN HANDLE THIS ON YOUR DEVICE:)", Toast.LENGTH_LONG).show();
        }
    }

    private void sendSms(String recipient, String text) {
        Intent intent = new Intent(Intent.ACTION_SENDTO); // ACTION_SEND usage scenario, who the data is being delivered is not specified.
        intent.setData(Uri.parse("smsto:" + recipient)); // Limited the app's who can handle the send action.
        intent.putExtra("sms_body", text);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "sendSms: NO APP CAN HANDLE THIS ON YOUR DEVICE:)");
            Toast.makeText(this, "NO APP CAN HANDLE THIS ON YOUR DEVICE:)", Toast.LENGTH_LONG).show();
        }
    }

    private void startWifiSettings() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "startWifiSettings: NO APP CAN HANDLE THIS ON YOUR DEVICE:)");
            Toast.makeText(this, "NO APP CAN HANDLE THIS ON YOUR DEVICE:)", Toast.LENGTH_LONG).show();
        }
    }

    private void searchOnWeb(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH).putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "searchOnWeb: NO APP CAN HANDLE THIS ON YOUR DEVICE:)");
            Toast.makeText(this, "NO APP CAN HANDLE THIS ON YOUR DEVICE:)", Toast.LENGTH_LONG).show();
        }
    }

    private void dialPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum)); // ACTION_CALL need CALL_PHONE permission, but without need user to press call button to make the phone call.
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "dialPhone: NO APP CAN HANDLE THIS ON YOUR DEVICE:)");
            Toast.makeText(this, "NO APP CAN HANDLE THIS ON YOUR DEVICE:)", Toast.LENGTH_LONG).show();
        }
    }

    private void createNote(String subject, String text) {
        Intent intent = new Intent(NoteIntents.ACTION_CREATE_NOTE);
        intent.putExtra(NoteIntents.EXTRA_NAME, subject);
        intent.putExtra(NoteIntents.EXTRA_TEXT, text);
        intent.setType("text/plain");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "createNote: NO APP CAN HANDLE THIS ON YOUR DEVICE:)");
            Toast.makeText(this, "NO APP CAN HANDLE THIS ON YOUR DEVICE:)", Toast.LENGTH_LONG).show();
        }
    }

    private void playMusicFromSearch(String artist) {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE); // set the search mode
        intent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist); // set the search mode corresponding value, the artist name to be searched.
        intent.putExtra(SearchManager.QUERY, artist); // set the unstructured search content for apps/system using old mechanism, required.
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "playMusicFromSearch: NO APP CAN HANDLE THIS ON YOUR DEVICE:)");
            Toast.makeText(this, "NO APP CAN HANDLE THIS ON YOUR DEVICE:)", Toast.LENGTH_LONG).show();
        }
    }

    private void playAudioFile() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ia800301.us.archive.org/14/items/art_of_war_librivox/art_of_war_01-02_sun_tzu.mp3"));
        intent.setType("audio/*");  // without this, it always resolved to browser
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showLocationOnMap() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:47.6,-122.3?z=10"));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No app can handle your request, unfortunately.", Toast.LENGTH_LONG).show();
        }
    }

    private void reserveCar() {
        int verifyGooglePlayServicesAvailableResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (verifyGooglePlayServicesAvailableResult != ConnectionResult.SUCCESS) {
            Log.d(TAG, "GOOGLE API IS NOT AVAILABLE: " + verifyGooglePlayServicesAvailableResult);
            GoogleApiAvailability.getInstance().getErrorDialog(this, verifyGooglePlayServicesAvailableResult, REQUEST_GOOGLE_PLAY_SERVICE_ERROR_FIX, null).show();
        }
        Intent intent = new Intent(ReserveIntents.ACTION_RESERVE_TAXI_RESERVATION); // NEED GOOGLE APIS/SERVICE SUPPORT
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No app can handle this, unfortunately", Toast.LENGTH_LONG).show();
        }
    }

    private void openImageFile() {
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
