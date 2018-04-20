package com.gqq.binderaidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gqq on 2018/4/20.
 */

public class User implements Parcelable {

    private int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // 反序列化方法
    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    // 序列化
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    // 内容描述
    @Override
    public int describeContents() {
        // 一般返回0，另一个特殊返回CONTENTS_FILE_DESCRIPTOR，为有FileDescriptor，放入Parcelable需指定。。
        // 然而。。好像并没有什么用，所以返回0就好了
        return 0;
    }

    // 反序列化
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            // 反序列化对象
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            // 反序列化数组
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
