package com.gqq.binderaidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MESSAGE_WHAT_ARRIVED = 1;
    private IUserManager userManager;
    private TextView textView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WHAT_ARRIVED:
                    Log.i("TAG", "received new user" + msg.obj);
                    List<User> userList = null;
                    try {
                        userList = userManager.getUserList();
                        textView.setText(userList.toString());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tv_show);
        findViewById(R.id.btn_client).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userManager == null) return;
                try {
                    userManager.addUser(new User(1, "gqq"));
                    List<User> userList = userManager.getUserList();
                    textView.setText(userList.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        bindUserService();

    }

    @Override
    protected void onDestroy() {
        if (userManager != null && userManager.asBinder().isBinderAlive()) {
            try {

                // 取消注册监听：会发现unregisterListener中remove的时候失败了
                userManager.unregisterListener(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindUserService();
        super.onDestroy();
    }

    private void bindUserService() {
        Intent intent = new Intent();
        intent.setAction("com.gqq.binderaidl.IUserManager");
        // Android 5.0 以后必须 显式启动,参考：https://blog.csdn.net/vrix/article/details/45289207
        intent.setPackage("com.gqq.binderaidl");
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void unbindUserService() {
        unbindService(connection);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MainActivity.this, "onServiceConnected", Toast.LENGTH_SHORT).show();
            userManager = IUserManager.Stub.asInterface(service);
            //  设置死亡代理
            try {

                userManager.registerListener(listener);

                service.linkToDeath(deathRecipient, 0);
            } catch (RemoteException e) {
                Log.i("TAG", "RemoteException");
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MainActivity.this, "onServiceDisConnected", Toast.LENGTH_SHORT).show();
            userManager = null;
        }
    };

    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (userManager == null) {
                return;
            }
            Log.i("TAG", "binderDied");
            userManager.asBinder().unlinkToDeath(deathRecipient, 0);
            userManager = null;
            // 重新绑定远程Service
            bindUserService();
        }
    };

    private IOnNewUserArrivedListener listener = new IOnNewUserArrivedListener.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void onNewUserArrived(User user) throws RemoteException {
            handler.obtainMessage(MESSAGE_WHAT_ARRIVED, user).sendToTarget();
        }
    };
}
