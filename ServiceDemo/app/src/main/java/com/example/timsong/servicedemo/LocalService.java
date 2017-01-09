package com.example.timsong.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Random;

public class LocalService extends Service {
    private IBinder mBinder = new LocalBinder();
    private Random mRandomGenerator = new Random();

    public LocalService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * In-application communication Binder.
     */
    public class LocalBinder extends Binder {
        public Service getService() {
            return LocalService.this;
        }
    }

    /**
     * Method for clients to call. Executed in main thread, take care.
     * @return A randomly generated integer between 0 and 99.
     */
    public int getRandomNum(){
        return mRandomGenerator.nextInt(100);
    }
}
