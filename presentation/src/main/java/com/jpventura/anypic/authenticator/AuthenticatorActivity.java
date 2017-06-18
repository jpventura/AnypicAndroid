package com.jpventura.anypic.authenticator;

import android.accounts.AccountAuthenticatorActivity;
import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.jpventura.anypic.R;

public class AuthenticatorActivity extends AccountAuthenticatorActivity implements
        AuthenticatorContract.View,
        View.OnClickListener {

    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "ARG_IS_ADDING_NEW_ACCOUNT";

    private LoginButton mLoginButton;
    private SignInButton mSignInButton;
    private AuthenticatorContract.Presenter mPresenter;

    @Override
    public void setPresenter(AuthenticatorContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_facebook_login:
                mPresenter.signIn("facebook");
                break;
        }
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_firebase_authenticator);

        mLoginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        mLoginButton.setOnClickListener(this);

        mSignInButton = (SignInButton) findViewById(R.id.button_google_login);

        setPresenter(new AuthenticatorPresenter(this, mLoginButton));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    protected void onStop() {
        mPresenter.stop();
        super.onStop();
    }

}
