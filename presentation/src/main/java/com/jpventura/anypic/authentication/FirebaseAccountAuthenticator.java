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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import static com.google.common.base.Preconditions.checkNotNull;

public class FirebaseAccountAuthenticator extends AbstractAccountAuthenticator {

    private final AccountManager mAccountManager;
    private final FirebaseAuth mAuthenticator;

    FirebaseAccountAuthenticator(@NonNull final Context context) {
        super(checkNotNull(context));
        mAccountManager = AccountManager.get(context);
        mAuthenticator = FirebaseAuth.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
                             String accountType,
                             String authTokenType,
                             String[] requiredFeatures,
                             Bundle options) throws NetworkErrorException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle addAccountFromCredentials(AccountAuthenticatorResponse response,
                                            Account account,
                                            Bundle accountCredentials) throws NetworkErrorException {
        return super.addAccountFromCredentials(response, account, accountCredentials);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
                                     Account account,
                                     Bundle options) throws NetworkErrorException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    public Task<Bundle> getAuthToken(Account account,
                                     String authTokenType,
                                     Bundle options) throws NetworkErrorException {

        final String customToken = mAccountManager.peekAuthToken(account, authTokenType);

        return mAuthenticator.signInWithCustomToken(customToken)
                .continueWithTask(new GetFirebaseTokenTask(true))
                .continueWith(new AssembleAuthTokenTask());
    }

    /**
     * {@inheritDoc}
     */
    @WorkerThread
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response,
                               Account account,
                               String authTokenType,
                               Bundle options) throws NetworkErrorException {
        String customToken = mAccountManager.peekAuthToken(account, authTokenType);
        mAuthenticator.signInWithCustomToken(mAccountManager.peekAuthToken(account, authTokenType));
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
                              Account account,
                              String[] features) throws NetworkErrorException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
                                    Account account,
                                    String authTokenType,
                                    Bundle options) throws NetworkErrorException {
        return null;
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
            result.putString(AccountManager.KEY_AUTHTOKEN, task.getResult().getToken());
            result.putString(AccountManager.KEY_AUTH_TOKEN_LABEL, authTokenType);

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
