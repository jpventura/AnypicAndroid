package com.jpventura.anypic;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.jpventura.util.accounts.GetAuthTokenCallable;

import java.io.IOException;
import java.io.OptionalDataException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class AccountLocalSource implements AccountSource {

    private static final Object LOCK = new Object();
    private static final String LOG_TAG = AccountLocalSource.class.getSimpleName();



    private static AccountSource sInstance;

    private final WeakReference<Context> mContext;
    private final HandlerThread mHandlerThread;
    private final AccountManager mManager;

    public static synchronized AccountSource getInstance(@NonNull Context context) {
        if (null == sInstance) {
            synchronized (LOCK) {
                sInstance = new AccountLocalSource(context);
            }
        }

        return sInstance;
    }

    private AccountLocalSource(@NonNull Context context) {
        mContext = new WeakReference<>(checkNotNull(context));
        mHandlerThread = new HandlerThread(LOG_TAG);
        mHandlerThread.start();
        mManager = AccountManager.get(mContext.get());
    }

    @Override
    public Task<Boolean> addAccount(@NonNull Account account, @NonNull String password, @NonNull Bundle userdata) {
        try {
            boolean x = mManager.addAccountExplicitly(account, password, userdata);

            if (x) {
                mManager.setAuthToken(account, "com.jpventura.anypic", "SmartMEI");
                mManager.setPassword(account, password);
            }

            return Tasks.forResult(x);
        } catch (NullPointerException e) {
            return Tasks.forException(new IllegalArgumentException(e));
        }
    }

    @Override
    public boolean hasAccount(@NonNull Account account) {
        final Account[] accounts = mManager.getAccounts();
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].equals(account)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Task<String> getAuthToken(@NonNull final Account account, @NonNull final Bundle options) {
        HandlerThread thread = new HandlerThread(AccountLocalSource.class.getSimpleName());
        thread.start();

//        final Handler handler = new Handler(thread.getLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                throw new IllegalStateException(this.getLooper().toString());
//            }
//        };

        final AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                throw new IllegalStateException(AccountManagerCallback.class.getSimpleName());
            }
        };

        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                Bundle result = mManager.getAuthToken(account, "com.jpventura.anypic", options, false, callback, null).getResult();

                if (null != result) {
                    return result.getString(AccountManager.KEY_AUTHTOKEN);
                }

                return null;
            }
        };

        return Tasks.call(Worker.THREAD_POOL_EXECUTOR, callable);

//        HandlerThread thread = new HandlerThread(AccountLocalSource.class.getSimpleName());
//        thread.start();
//
//        mManager.getAuthToken(account, "com.jpventura.anypic", options, true, new AccountManagerCallback<Bundle>() {
//            @Override
//            public void run(AccountManagerFuture<Bundle> future) {
//                try {
//                    Bundle result = future.getResult(2000, TimeUnit.MILLISECONDS);
//                    taskCompletionSource.setResult(result.getString(AccountManager.KEY_AUTHTOKEN, "não tem"));
//                } catch (AuthenticatorException e) {
//                    taskCompletionSource.setException(e);
//                } catch (IOException e) {
//                    taskCompletionSource.setException(e);
//                } catch (NullPointerException e) {
//                    taskCompletionSource.setException(new IllegalArgumentException(e));
//                } catch (OperationCanceledException e) {
//                    taskCompletionSource.setException(e);
//                }
//            }
//        }, new Handler(thread.getLooper()));
//
//
//        return taskCompletionSource.getTask();
    }

    @SuppressWarnings("deprecation")
    @Override
    public Task<Boolean> removeAccount(@NonNull final Account account) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            try {
                return Tasks.forResult(mManager.removeAccountExplicitly(checkNotNull(account)));
            } catch (NullPointerException e) {
                return Tasks.forException(e);
            }
        }

        final TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

        mManager.removeAccount(checkNotNull(account), new AccountManagerCallback<Boolean>() {
            @Override
            public void run(AccountManagerFuture<Boolean> future) {
                try {
                    taskCompletionSource.setResult(future.getResult());
                } catch (AuthenticatorException e) {
                    taskCompletionSource.setException(e);
                } catch (IOException e) {
                    taskCompletionSource.setException(e);
                } catch (NullPointerException e) {
                    taskCompletionSource.setException(new IllegalArgumentException(e));
                } catch (OperationCanceledException e) {
                    taskCompletionSource.setException(e);
                }
            }
        }, null);

        return taskCompletionSource.getTask();
    }

}
