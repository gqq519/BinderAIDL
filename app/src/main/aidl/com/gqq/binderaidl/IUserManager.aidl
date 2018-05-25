// IUserManager.aidl
package com.gqq.binderaidl;

// Declare any non-default types here with import statements
import com.gqq.binderaidl.User;
import com.gqq.binderaidl.IOnNewUserArrivedListener;

interface IUserManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void addUser(in User user);
    List<User> getUserList();
    void registerListener(IOnNewUserArrivedListener listener);
    void unregisterListener(IOnNewUserArrivedListener listener);
}
