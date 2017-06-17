package com.jpventura.anypic.authenticator;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Intent;
import android.os.Bundle;

import com.jpventura.anypic.R;

public class AuthenticatorActivity extends AccountAuthenticatorActivity implements AuthenticatorContract.View {

    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "ARG_IS_ADDING_NEW_ACCOUNT";

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
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_firebase_authenticator);

        setPresenter(new AuthenticatorPresenter(this));
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
