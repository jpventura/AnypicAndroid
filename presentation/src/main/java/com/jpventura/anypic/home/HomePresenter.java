package com.jpventura.anypic.home;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.jpventura.anypic.authentication.FirebaseAuthenticator;

import java.lang.ref.WeakReference;

import static com.google.common.base.Preconditions.checkNotNull;

class HomePresenter implements HomeContract.Presenter {

    private static final String LOG_TAG = HomePresenter.class.getSimpleName();

    private final WeakReference<HomeActivity> mActivity;
    private final AccountManager mManager;
    private final FirebaseAuthenticator mAuthenticator;

    HomePresenter(HomeActivity activity) {
        mActivity = new WeakReference<>(activity);
        mManager = AccountManager.get(activity);
        mAuthenticator = FirebaseAuthenticator.getInstance(activity);
    }

    @Override
    public Task<Bundle> getAuthToken(@NonNull Account account) {
        Log.d(LOG_TAG, "getAuthToken(" + account.toString() + ")");
        return mAuthenticator.getAuthToken(checkNotNull(account), "com.jpventura.anypic", null, mActivity.get());
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

}
