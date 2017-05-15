package com.jpventura.util.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;

public class GetAuthTokenCallable implements AccountManagerCallback<Bundle>, Callable<Bundle> {

    private static final String LOG_TAG = GetAuthTokenCallable.class.getSimpleName();

    private final Account mAccount;
    private final WeakReference<Context> mContext;
    private final String mAuthTokenType;
    private final Bundle mOptions;

    public GetAuthTokenCallable(@NonNull final Account account,
                         @NonNull String authTokenType,
                         @Nullable Bundle options,
                         @NonNull Context context) {
        mAccount = checkNotNull(account);
        mContext = new WeakReference<>(checkNotNull(context));
        mAuthTokenType = checkNotNull(authTokenType);
        mOptions = options;
    }

    @Override
    @WorkerThread
    public Bundle call() throws Exception {
        return execute().getResult();
    }

    @Override
    public void run(AccountManagerFuture<Bundle> future) {
        Log.d(LOG_TAG, "{ done: " + Boolean.toString(future.isDone()) + ", canceled: " + Boolean.toString(future.isCancelled()) + "}");
    }

    private AccountManagerFuture<Bundle> execute() {
        final AccountManager manager = AccountManager.get(mContext.get());
        return manager.getAuthToken(mAccount, mAuthTokenType, mOptions, false, null, null);

    }

}
