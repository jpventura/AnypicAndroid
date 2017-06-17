package com.jpventura.anypic.accounts;

import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;

import com.jpventura.anypic.R;

public class FirebaseAuthenticatorActivity extends AccountAuthenticatorActivity {

    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "ARG_IS_ADDING_NEW_ACCOUNT";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_firebase_authenticator);
        finish();
    }

}
