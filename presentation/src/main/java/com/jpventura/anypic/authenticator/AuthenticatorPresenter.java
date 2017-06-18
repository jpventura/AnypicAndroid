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

package com.jpventura.anypic.authenticator;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.ref.WeakReference;

class AuthenticatorPresenter implements AuthenticatorContract.Presenter,
        OnFailureListener,
        OnSuccessListener<AuthResult> {

    private final WeakReference<AuthenticatorActivity> mActivity;
    private final FacebookController mFacebookController;

    AuthenticatorPresenter(AuthenticatorActivity activity, LoginButton loginButton) {
        mActivity = new WeakReference<>(activity);
        mFacebookController = new FacebookController(loginButton);

        // FIXME
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mActivity.get().finish();
        }
    }

    @Override
    public void onSuccess(AuthResult authResult) {
        mActivity.get().finish();
        Toast.makeText(mActivity.get(), authResult.getUser().getDisplayName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(mActivity.get(), e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void result(int requestCode, int resultCode, Intent result) {
        mFacebookController.result(requestCode, resultCode, result);
    }

    @Override
    public void signIn(@NonNull String provider) {
        mFacebookController.execute()
                .addOnFailureListener(this)
                .addOnSuccessListener(this);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

}
