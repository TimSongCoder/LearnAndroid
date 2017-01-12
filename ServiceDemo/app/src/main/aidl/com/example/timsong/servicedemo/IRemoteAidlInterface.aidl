// IRemoteAidlInterface.aidl
package com.example.timsong.servicedemo;

// Declare any non-default types here with import statements

interface IRemoteAidlInterface {

    int getPid();

    void serve(String param);
}
