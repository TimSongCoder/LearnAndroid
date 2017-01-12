package com.example.timsong.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

public class RemoteAidlService extends Service {
    private static final String TAG = "RemoteAidlService";
    private RemoteAidlCallback mRemoteServiceDestoryCallback;

    public RemoteAidlService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind is called: " + intent);
        return new IRemoteAidlInterface.Stub() {
            @Override
            public int getPid() throws RemoteException {
                return Process.myPid();
            }

            @Override
            public void serve(String param) throws RemoteException {
                Log.d(TAG, "IRemoteAidlInterface.serve() is called with param: " + param + " in thread: " + Thread.currentThread());
            }

            @Override
            public void registerServiceDestroyCallback(RemoteAidlCallback callback) throws RemoteException {
                mRemoteServiceDestoryCallback = callback;
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy is called, in the thread: " + Thread.currentThread());
        try {
            mRemoteServiceDestoryCallback.onServiceDestroyed();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
