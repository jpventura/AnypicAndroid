package com.jpventura.anypic.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class FirebaseAuthenticator {

    private static final Object LOCK = new Object();
    private static final String LOG_TAG = FirebaseAuthenticator.class.getSimpleName();

    private static FirebaseAuthenticator sFirebaseAuthenticator;

    private final WeakReference<Context> mContext;
    private final HandlerThread mHandlerThread;
    private final AccountManager mManager;

    public static synchronized FirebaseAuthenticator getInstance(@NonNull final Context context) {
        if (null == sFirebaseAuthenticator) {
            synchronized (LOCK) {
                sFirebaseAuthenticator = new FirebaseAuthenticator(context);
            }
        }

        return sFirebaseAuthenticator;
    }

    private FirebaseAuthenticator(@NonNull final Context context) {
        mContext = new WeakReference<>(context);
        mHandlerThread = new HandlerThread(LOG_TAG);
        mHandlerThread.start();
        mManager = AccountManager.get(context);
    }

    private class GetAuthTokenTask implements Continuation<AuthResult, Task<GetTokenResult>> {

        private final boolean mForceRefresh;

        GetAuthTokenTask(final boolean forceRefresh) {
            mForceRefresh = forceRefresh;
        }

        @Override
        public Task<GetTokenResult> then(@NonNull Task<AuthResult> task) throws Exception {
            return task.getResult().getUser().getToken(mForceRefresh);
        }

    }

    public Task<Bundle> getAuthToken(@NonNull final Account account,
                                     @NonNull String authTokenType,
                                     @Nullable Bundle options,
                                     @NonNull Activity activity) {

        String authToken = mManager.peekAuthToken(account, authTokenType);


        Log.d(LOG_TAG, "getAuthToken");

        Bundle result = new Bundle();
        result.putString(AccountManager.KEY_AUTHTOKEN, "circular");

        return Tasks.forResult(result);

//        final TaskCompletionSource<Bundle> tcs = new TaskCompletionSource<>();
//
//        final Handler handler = new Handler(activity.getMainLooper());
//
//        final AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
//
//            @Override
//            public void run(AccountManagerFuture<Bundle> future) {
//                try {
//                    tcs.setResult(future.getResult());
//                } catch (AuthenticatorException e) {
//                    tcs.setException(e);
//                } catch (IOException e) {
//                    tcs.setException(e);
//                } catch (OperationCanceledException e) {
//                    tcs.setException(e);
//                }
//            }
//
//        };
//
//        mManager.getAuthToken(account, authTokenType, options, activity, callback, handler);

//        return tcs.getTask();
    }

}
