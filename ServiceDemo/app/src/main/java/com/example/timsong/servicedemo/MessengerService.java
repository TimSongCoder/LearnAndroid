package com.example.timsong.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class MessengerService extends Service {
    public static final int MSG_TOAST = 1;
    public static final int MSG_DONATION = 2;
    private static final String TAG = "MessengerService";
    private static final int MSG_DONATION_RESULT_OK = 3;
    private Messenger mIncomingMessenger = new Messenger(new IncomingMsgHandler());
    public MessengerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind is called: " + intent);
        return mIncomingMessenger.getBinder();
    }

    public class IncomingMsgHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_TOAST:
                    Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case MSG_DONATION:
                    donate(msg);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Mock the donate service.
     * Need send result message to client after the service if the client indicated.
     * @param msg Message object containing the donation detail.
     */
    private void donate(Message msg) {
        // MY SERVICE BEGIN--
        Log.i(TAG, "TRANSACTION HAS COMPLETED, with amount: " + msg.arg1);
        // MY SERVICE END--

        // RESULT REPLY LOGIC
        Messenger replyMessenger = msg.replyTo;
        if(replyMessenger != null){
            Message message = Message.obtain();
            message.what = MSG_DONATION_RESULT_OK;
            try {
                replyMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
