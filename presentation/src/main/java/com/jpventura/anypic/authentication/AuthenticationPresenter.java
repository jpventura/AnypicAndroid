/*
 * Copyright (c) 2017 Joao Paulo Fernandes Ventura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jpventura.anypic.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;

import com.google.firebase.auth.GetTokenResult;
import com.jpventura.anypic.authentication.AuthenticationContract.Presenter;

import java.lang.ref.WeakReference;

import static com.google.common.base.Preconditions.checkNotNull;

public class AuthenticationPresenter implements AuthStateListener, Presenter {

    private static final String LOG_TAG = AuthenticationPresenter.class.getSimpleName();

    private final AccountManager mAccountManager;
    private final FirebaseAuth mAuth;
    private WeakReference<AuthenticationActivity> mActivity;

    AuthenticationPresenter(AuthenticationActivity activity) {
        mAccountManager = AccountManager.get(activity);
        mActivity = new WeakReference<>(activity);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth authenticator) {
        Log.e(LOG_TAG, authenticator.toString());
    }

    @Override
    public void start() {
        mAuth.addAuthStateListener(this);
    }

    @Override
    public void stop() {
        mAuth.removeAuthStateListener(this);
    }

    @Override
    public Task<Account> signUp(@NonNull final String email, @NonNull final String password) {
        final Account account = new Account(email, "com.jpventura.anypic");

        return Tasks.<Void>forResult(null)
                .continueWithTask(new SignUpWithEmailAndPasswordTask(email, password))
                .continueWithTask(new GetAuthTokenTask(false))
                .continueWithTask(new AddAccountExplicitlyTask(account, password));
    }

    @Override
    public Task<AuthResult> signIn(@NonNull String email, @NonNull String password) {
        email = checkNotNull(email);
        password = checkNotNull(password);
        return mAuth.signInWithEmailAndPassword(email, password);
    }


    private class AddAccountExplicitlyTask implements Continuation<GetTokenResult, Task<Account>> {

        private static final String ERROR_MESSAGE = "Failed to add account explicitly";

        private final Account mAccount;
        private final String mPassword;
        private final TaskCompletionSource<Account> mTaskCompletionSource;

        AddAccountExplicitlyTask(@NonNull final Account account, @NonNull final String password) {
            mAccount = checkNotNull(account);
            mPassword = checkNotNull(password);
            mTaskCompletionSource = new TaskCompletionSource<>();
        }

        @Override
        public Task<Account> then(@NonNull Task<GetTokenResult> task) throws Exception {
            Log.d(LOG_TAG, AddAccountExplicitlyTask.class.getSimpleName());

            final String authToken = task.getResult().getToken();

            if (mAccountManager.addAccountExplicitly(mAccount, mPassword, null)) {
                // FIXME
                mAccountManager.setAuthToken(mAccount, "tokentype", authToken);
                mAccountManager.setPassword(mAccount, mPassword);

                mTaskCompletionSource.setResult(mAccount);
            } else {
                mTaskCompletionSource.setException(new AuthenticatorException(ERROR_MESSAGE));
            }

            return mTaskCompletionSource.getTask();
        }

    }

    private class GetAuthTokenTask implements Continuation<AuthResult, Task<GetTokenResult>> {

        private final boolean mForceRefresh;

        GetAuthTokenTask(final boolean forceRefresh) {
            mForceRefresh = forceRefresh;
        }

        @Override
        public Task<GetTokenResult> then(@NonNull Task<AuthResult> task) throws Exception {
            Log.d(LOG_TAG, GetAuthTokenTask.class.getSimpleName());
            return task.getResult().getUser().getToken(mForceRefresh);
        }

    }

    private class SignUpWithEmailAndPasswordTask implements Continuation<Void, Task<AuthResult>> {

        private final String mEmail;
        private final String mPassword;

        SignUpWithEmailAndPasswordTask(@NonNull final String email, @NonNull final String password) {
            mEmail = checkNotNull(email);
            mPassword = checkNotNull(password);
        }

        @Override
        public Task<AuthResult> then(@NonNull Task<Void> task) throws Exception {
            Log.d(LOG_TAG, SignUpWithEmailAndPasswordTask.class.getSimpleName());
            return mAuth.createUserWithEmailAndPassword(mEmail, mPassword);
        }

    }

}
