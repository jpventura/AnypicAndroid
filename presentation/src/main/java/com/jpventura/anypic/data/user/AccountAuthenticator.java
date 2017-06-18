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

package com.jpventura.anypic.data.user;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.google.android.gms.tasks.Tasks;
import com.jpventura.anypic.accounts.SignInTask;
import com.jpventura.anypic.authenticator.AuthenticatorActivity;

import java.lang.annotation.Retention;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static android.accounts.AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE;
import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static android.accounts.AccountManager.KEY_INTENT;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class AccountAuthenticator extends AbstractAccountAuthenticator {

    public static final String KEY_AUTH_TOKEN_TYPE = "authTokenType";

    @Retention(SOURCE)
    @StringDef({AUTH_TOKEN_EMAIL, AUTH_TOKEN_FACEBOOK, AUTH_TOKEN_GOOGLE})
    public @interface AuthTokenType {}
    public static final String AUTH_TOKEN_EMAIL = "email";
    public static final String AUTH_TOKEN_FACEBOOK = "facebook";
    public static final String AUTH_TOKEN_GOOGLE = "google";

    private final Context mContext;
    private final Map<String, Callable<Bundle>> mProviders;

    public AccountAuthenticator(@NonNull Context context) {
        this(context, new HashMap<String, Callable<Bundle>>());
    }

    public AccountAuthenticator(@NonNull Context context,
                                @NonNull Map<String, Callable<Bundle>> providers) {
        super(context);
        mContext = context;
        mProviders = providers;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
                             @NonNull String accountType,
                             @NonNull String authTokenType,
                             String[] requiredFeatures,
                             Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(KEY_AUTH_TOKEN_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
                                     @NonNull Account account,
                                     Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response,
                                 @NonNull String accountType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response,
                               @NonNull Account account,
                               @NonNull String authTokenType,
                               Bundle options) throws NetworkErrorException {
        if (!isAuthTokenTypeSupported(authTokenType)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager accountManager = AccountManager.get(mContext);
        final String authToken = accountManager.peekAuthToken(account, authTokenType);
        Bundle authData = new Bundle();

        // Ensure authentication token has not expired
        if (!TextUtils.isEmpty(authToken)) {
            authData.putString(KEY_ACCOUNT_NAME, account.name);
            authData.putString(KEY_ACCOUNT_TYPE, account.type);
            authData.putString(KEY_AUTHTOKEN, authToken);

            try {
                authData = Tasks.await(Tasks.forResult(authData).continueWithTask(new SignInTask(true)));
            } catch (Exception e) {
                authData.clear();
            }
        }

        // If we get an authToken - we return it
        if (authData.containsKey(KEY_AUTHTOKEN)) {
            return authData;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(KEY_ACCOUNT_NAME, account.name);
        intent.putExtra(KEY_ACCOUNT_TYPE, account.type);
        intent.putExtra(KEY_AUTH_TOKEN_TYPE, authTokenType);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public @AuthTokenType String getAuthTokenLabel(String authTokenType) {
        switch (authTokenType) {
            case AUTH_TOKEN_EMAIL:
                return AUTH_TOKEN_EMAIL;

            case AUTH_TOKEN_FACEBOOK:
                return AUTH_TOKEN_FACEBOOK;

            default:
                return authTokenType + " (Label)";
        }
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
                              @NonNull Account account,
                              String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
                                    @NonNull Account account,
                                    @NonNull String authTokenType,
                                    Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    private boolean isAuthTokenTypeSupported(String authTokenType) {
        switch (authTokenType) {
            case AUTH_TOKEN_EMAIL:
            case AUTH_TOKEN_FACEBOOK:
            case AUTH_TOKEN_GOOGLE:
                return true;

            default:
                return false;
        }
    }

}
