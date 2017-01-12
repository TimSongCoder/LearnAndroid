// IRemoteAidlInterface.aidl
package com.example.timsong.servicedemo;

// Declare any non-default types here with import statements
import com.example.timsong.servicedemo.RemoteAidlCallback;

interface IRemoteAidlInterface {

    int getPid();

    void serve(String param);

    void registerServiceDestroyCallback(RemoteAidlCallback callback);
}
