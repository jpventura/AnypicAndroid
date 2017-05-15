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

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.jpventura.util.accounts.GetAuthTokenCallable;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static android.accounts.AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE;
import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_AUTH_TOKEN_LABEL;
import static android.accounts.AccountManager.KEY_ERROR_MESSAGE;
import static android.accounts.AccountManager.KEY_INTENT;

import static com.google.common.base.Preconditions.checkNotNull;

class FirebaseAccountAuthenticator extends AbstractAccountAuthenticator {

    private static final String LOG_TAG = FirebaseAccountAuthenticator.class.getSimpleName();

    private final AccountManager mAccountManager;
    private final FirebaseAuth mAuthenticator;
    private final WeakReference<Context> mContext;

    FirebaseAccountAuthenticator(@NonNull final Context context) {
        super(checkNotNull(context));
        mAccountManager = AccountManager.get(context);
        mAuthenticator = FirebaseAuth.getInstance();
        mContext = new WeakReference<>(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
                             String accountType,
                             String authTokenLabel,
                             String[] requiredFeatures,
                             Bundle options) throws NetworkErrorException {
        Log.d(LOG_TAG, "addAccount");

        final Intent intent = new Intent(mContext.get(), AuthenticatorActivity.class);
        intent.putExtra(KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(KEY_AUTH_TOKEN_LABEL, authTokenLabel);

        final Bundle authToken = new Bundle();
        authToken.putParcelable(KEY_INTENT, intent);

        return authToken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle addAccountFromCredentials(AccountAuthenticatorResponse response,
                                            Account account,
                                            Bundle accountCredentials) throws NetworkErrorException {
        throw new UnsupportedOperationException("Add account from credentials");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
                                     Account account,
                                     Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException("Confirm credentials");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        throw new UnsupportedOperationException("Edit properties");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle getAuthToken(final AccountAuthenticatorResponse response,
                               final Account account,
                               String authTokenType,
                               Bundle options) throws NetworkErrorException {
        Log.e(LOG_TAG, "getAuthToken(" + account.toString() + ", " + authTokenType + ")");

        final Bundle result = new Bundle();
        result.putString(KEY_ACCOUNT_NAME, account.name);
        result.putString(KEY_ACCOUNT_TYPE, account.type);
        result.putString(KEY_AUTH_TOKEN_LABEL, authTokenType);
        result.putString(KEY_AUTHTOKEN, "xuasdfasdfadsfxu");

        if (null != response) {
            response.onResult(result);
        } else {
            Log.e(LOG_TAG, "response eh null");
        }

        Log.e(LOG_TAG, "deveria retornar " + result.toString());

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
                              Account account,
                              String[] features) throws NetworkErrorException {
        throw new UnsupportedOperationException("Has features");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Hash code");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthTokenLabel(String authTokenType) {
        throw new UnsupportedOperationException("Get auth token label");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
                                    Account account,
                                    String authTokenType,
                                    Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException("Update credentials");
    }

    private class AssembleAuthTokenTask implements Continuation<GetTokenResult, Bundle> {

        @Override
        public Bundle then(@NonNull Task<GetTokenResult> task) throws Exception {
            final String accountName = mAuthenticator.getCurrentUser().getEmail();
            final String accountType = "com.jpventura.anypic";
            final String authTokenType = "email";
            final Bundle result = new Bundle();

            result.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
            result.putString(KEY_AUTHTOKEN, task.getResult().getToken());

            return result;
        }

    }

    private class GetFirebaseTokenTask implements Continuation<AuthResult, Task<GetTokenResult>> {

        private final boolean mForceRefresh;

        GetFirebaseTokenTask(final boolean forceRefresh) {
            mForceRefresh = forceRefresh;
        }

        @Override
        public Task<GetTokenResult> then(@NonNull Task<AuthResult> task) throws Exception {
            return task.getResult().getUser().getToken(mForceRefresh);
        }

    }

}
