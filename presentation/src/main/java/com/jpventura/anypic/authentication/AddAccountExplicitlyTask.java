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
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.GetTokenResult;

import static com.google.common.base.Preconditions.checkNotNull;

class AddAccountExplicitlyTask implements Continuation<GetTokenResult, Task<Account>> {

    private static final String ERROR_MESSAGE = "Failed to add account explicitly";

    private final Account mAccount;
    private final AccountManager mAccountManager;
    private final String mPassword;
    private final TaskCompletionSource<Account> mTaskCompletionSource;

    AddAccountExplicitlyTask(@NonNull final AccountManager accountManager,
                             @NonNull final Account account,
                             @NonNull final String password) {
        mAccount = checkNotNull(account);
        mAccountManager = checkNotNull(accountManager);
        mPassword = checkNotNull(password);
        mTaskCompletionSource = new TaskCompletionSource<>();
    }

    @Override
    public Task<Account> then(@NonNull Task<GetTokenResult> task) throws Exception {
        if (mAccountManager.addAccountExplicitly(mAccount, mPassword, null)) {
            mTaskCompletionSource.setResult(mAccount);
        } else {
            mTaskCompletionSource.setException(new AuthenticatorException(ERROR_MESSAGE));
        }

        return mTaskCompletionSource.getTask();
    }

}
