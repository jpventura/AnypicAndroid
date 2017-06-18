package com.jpventura.anypic.authenticator;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;

public class FacebookController implements FacebookCallback<LoginResult> {

    private static final Object LOCK = new Object();

    private final CallbackManager mCallbackManager;
    private TaskCompletionSource<AuthCredential> mTaskCompletionSource;

    public FacebookController(@NonNull LoginButton loginButton) {
        mCallbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, this);
    }

    public synchronized Task<AuthResult> execute() {
        if (null == mTaskCompletionSource) {
            synchronized (LOCK) {
                mTaskCompletionSource = new TaskCompletionSource<>();
            }
        }

        return mTaskCompletionSource.getTask()
                .continueWithTask(new SignInWithAuthCredentialTask());
    }

    @Override
    public synchronized void onCancel() {
        if (null == mTaskCompletionSource) return;
        mTaskCompletionSource.setException(new InterruptedException());
    }

    @Override
    public synchronized void onError(FacebookException e) {
        if (null == mTaskCompletionSource) return;
        mTaskCompletionSource.setException(e);
        mTaskCompletionSource = null;
    }

    @Override
    public synchronized void onSuccess(LoginResult loginResult) {
        mTaskCompletionSource.setResult(getCredential(loginResult));
        mTaskCompletionSource = null;
    }

    public void result(int requestCode, int resultCode, Intent result) {
        if (0xface != requestCode) return;
        mCallbackManager.onActivityResult(requestCode, resultCode, result);
    }

    private static AuthCredential getCredential(LoginResult loginResult) {
        final String accessToken = loginResult.getAccessToken().getToken();
        return FacebookAuthProvider.getCredential(accessToken);
    }

}
