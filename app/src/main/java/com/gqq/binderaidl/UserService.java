package com.gqq.binderaidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gqq on 2018/4/20.
 */

public class UserService extends Service {

    private List<User> users;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        users = new ArrayList<>();
        return new UserServiceImpl();
    }

    public class UserServiceImpl extends IUserManager.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void addUser(User user) throws RemoteException {
            user.name += "-Service";
            users.add(user);
        }

        @Override
        public List<User> getUserList() throws RemoteException {
            return users;
        }
    }
}
