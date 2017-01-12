// RemoteAidlCallback.aidl
package com.example.timsong.servicedemo;

// Declare any non-default types here with import statements
/**
* Callback can be used if you want to know when remote service is destroyed.
*/
interface RemoteAidlCallback {
    void onServiceDestroyed();
}
