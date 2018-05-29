package com.gqq.binderaidl;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
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

    // 他并不是一个list，并不能像操作list一样
    private RemoteCallbackList<IOnNewUserArrivedListener> listeners = new RemoteCallbackList<>();
    private boolean isServiceDestoryed = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        第一种
//        int check = checkCallingOrSelfPermission("com.gqq.binderaidl.permission.ACCESS_USER_SERVICE");
//        Log.i("TAG", "onBind check = "+check);
//        if (check == PackageManager.PERMISSION_DENIED) {
//            return null;
//        }
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
        int size = listeners.beginBroadcast();
        for (int i = 0; i < size; i++) {
            IOnNewUserArrivedListener onNewUserArrivedListener = listeners.getBroadcastItem(i);
            if (onNewUserArrivedListener != null) {
                onNewUserArrivedListener.onNewUserArrived(user);
            }
        }
        listeners.finishBroadcast();
    }

    public class UserServiceImpl extends IUserManager.Stub {

        // 第二种
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            // 权限验证
            int check = checkCallingOrSelfPermission("com.gqq.binderaidl.permission.ACCESS_USER_SERVICE");
            if(check == PackageManager.PERMISSION_DENIED){
                Log.i("TAG", "Binder 权限验证失败");
                return false;
            }
            // 包名验证
            String packageName=null;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if(packages!=null && packages.length>0){
                packageName = packages[0];
            }
            if(!packageName.startsWith("com.gqq")){
                Log.i("TAG", "包名验证失败");
                return false;

            }
            return super.onTransact(code, data, reply, flags);
        }

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
            listeners.register(listener);

            // 注意：

            int i = listeners.beginBroadcast();
            listeners.finishBroadcast();
            Log.i("TAG", "registerListener listener size:" + i);
        }

        @Override
        public void unregisterListener(IOnNewUserArrivedListener listener) throws RemoteException {
            listeners.unregister(listener);
            int i = listeners.beginBroadcast();
            listeners.finishBroadcast();
            Log.i("TAG", "unregisterListener listener size:" + i);
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
                User user = new User(userId, "new User:" + userId);
                try {
                    onNewUserArrived(user);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
