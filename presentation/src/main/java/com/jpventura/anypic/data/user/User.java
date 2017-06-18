package com.jpventura.anypic.data.user;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import static com.google.common.base.Preconditions.checkNotNull;

public class User extends Account implements Parcelable {

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel source) {
        super(source);
    }

    public User(@NonNull final FirebaseUser user) {
        super(checkNotNull(user).getEmail(), "com.jpventura.anypic");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

}
