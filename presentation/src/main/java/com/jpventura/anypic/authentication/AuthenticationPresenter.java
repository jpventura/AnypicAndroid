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

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;

import com.jpventura.anypic.authentication.AuthenticationContract.Presenter;

import java.lang.ref.WeakReference;

import static com.google.common.base.Preconditions.checkNotNull;

public class AuthenticationPresenter implements AuthStateListener, Presenter {

    private FirebaseAuth mAuth;
    private WeakReference<AuthenticationActivity> mActivity;

    AuthenticationPresenter(AuthenticationActivity activity) {
        mActivity = new WeakReference<>(activity);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth authenticator) {
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
    public Task<AuthResult> signUp(@NonNull final String email, @NonNull final String password) {
        return mAuth.createUserWithEmailAndPassword(checkNotNull(email), checkNotNull(password));
    }

    @Override
    public Task<AuthResult> signIn(@NonNull final String email, @NonNull final String password) {
        return mAuth.signInWithEmailAndPassword(checkNotNull(email), checkNotNull(password));
    }

}
