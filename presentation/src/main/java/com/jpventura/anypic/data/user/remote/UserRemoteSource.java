package com.jpventura.anypic.data.user.remote;

import android.database.ContentObservable;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.jpventura.anypic.data.user.UserSource;

public class UserRemoteSource extends ContentObservable implements AuthStateListener, UserSource {

    public UserRemoteSource() {
        super();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        try {
            Uri uri = new Uri.Builder()
                    .authority("com.jpventura.anypic")
                    .appendPath("users")
                    .appendPath(firebaseAuth.getCurrentUser().getUid())
                    .build();
            dispatchChange(true, uri);
        } catch (NullPointerException e) {

        }

    }

}
