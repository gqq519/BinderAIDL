package com.gqq.binderaidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gqq on 2018/4/20.
 */

public class UserService extends Service {

    private List<User> users;
    private List<IOnNewUserArrivedListener> listeners = new ArrayList<>();
    private boolean isServiceDestoryed = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        users = new ArrayList<>();
        return new UserServiceImpl();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new ServiceWorker()).start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceDestoryed = true;
    }

    private void onNewUserArrived(User user) throws RemoteException {
        users.add(user);
        for (int i = 0; i < listeners.size(); i++) {
            IOnNewUserArrivedListener onNewUserArrivedListener = listeners.get(i);
            onNewUserArrivedListener.onNewUserArrived(user);
        }
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

        @Override
        public void registerListener(IOnNewUserArrivedListener listener) throws RemoteException {

            if (!listeners.contains(listener)) {
                listeners.add(listener);
            } else {
                Log.i("TAG", "listener already exists");
            }
        }

        @Override
        public void unregisterListener(IOnNewUserArrivedListener listener) throws RemoteException {
            // 删除注册的监听失败了
            if (listeners.contains(listener)) {
                listeners.remove(listener);
            } else {
                Log.i("TAG", "not found");
            }
        }
    }

    public class ServiceWorker implements Runnable {

        @Override
        public void run() {
            while (!isServiceDestoryed) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int userId = users.size() + 1;
                User user = new User(userId, "new User:"+userId);
                try {
                    onNewUserArrived(user);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
