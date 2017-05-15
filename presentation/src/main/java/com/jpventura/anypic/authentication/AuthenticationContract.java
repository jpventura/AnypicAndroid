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
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.jpventura.anypic.BasePresenter;
import com.jpventura.anypic.BaseView;

public class AuthenticationContract {

    interface Presenter extends BasePresenter {

        Task<AuthResult> signIn(@NonNull String email, @NonNull String password);

        Task<Bundle> signUp(@NonNull final String email, @NonNull final String password);

    }

    interface View extends BaseView<Presenter> {

        @Override
        void setPresenter(Presenter presenter);

    }

}
