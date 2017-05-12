package com.jpventura.anypic.authentication;

import java.lang.ref.WeakReference;

public class AuthenticationPresenter implements AuthenticationContract.Presenter {

    private WeakReference<AuthenticationActivity> mActivity;

    AuthenticationPresenter(AuthenticationActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

}
