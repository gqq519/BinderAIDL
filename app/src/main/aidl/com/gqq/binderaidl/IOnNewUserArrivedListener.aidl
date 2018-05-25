// IOnNewBookArrivedListener.aidl
package com.gqq.binderaidl;

import com.gqq.binderaidl.User;

// Declare any non-default types here with import statements

interface IOnNewUserArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    void onNewUserArrived(in User user);
}
