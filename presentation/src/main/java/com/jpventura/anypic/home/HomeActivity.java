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

package com.jpventura.anypic.home;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.jpventura.anypic.R;
import com.jpventura.anypic.authentication.AuthenticatorActivity;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity implements HomeContract.View {

    private static final String LOG_TAG = HomeActivity.class.getSimpleName();

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setPresenter(new HomePresenter(this));

        findViewById(R.id.token).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mPresenter.getAuthToken(new Account("maria@email.com", "com.jpventura.anypic"))
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                                Log.e(LOG_TAG, e.getMessage());
//                            }
//                        })
//                        .addOnSuccessListener(new OnSuccessListener<Bundle>() {
//                            @Override
//                            public void onSuccess(Bundle bundle) {
//                                Toast.makeText(getApplicationContext(), bundle.toString(), Toast.LENGTH_LONG).show();
//                                Log.d(LOG_TAG, bundle.toString());
//                            }
//                        });


                AccountManager manager = AccountManager.get(getApplicationContext());


                final Account account = manager.getAccounts()[0];
                final String authTokenType = "com.jpventura.anypic";
                final Boolean notifyAuthFailure = true;

                String token = manager.peekAuthToken(account, authTokenType);
                if (TextUtils.isEmpty(token)) {
                    token = "vazio";
                }

                Log.e(LOG_TAG, "1. peek token " + token);
                manager.setAuthToken(account, "com.jpventura.anypic", "banana123");

                token = manager.peekAuthToken(account, authTokenType);
                Log.e(LOG_TAG, "2. peek token " + token);

//
                AccountManager.get(getApplicationContext()).getAuthToken(account, authTokenType, new Bundle(), HomeActivity.this, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Log.e(LOG_TAG, "blocking token AccountManagerFuture" + account.toString());

                        try {
                            int i = 0;
                            while (!future.isDone()) {
                                Log.d("ventura", "busy waiting " + Integer.toString(i++));
                            }

                            Bundle result = future.getResult();
                            result = (null == result ? new Bundle() : result);
                            Log.e(LOG_TAG, "blocking token " + result.getString(AccountManager.KEY_AUTHTOKEN, "nao tem nada"));
                        } catch (InterruptedIOException e) {
                            Log.e(LOG_TAG, e.getMessage());
                        } catch (OperationCanceledException e) {
                            Log.e(LOG_TAG, e.getMessage());
                        } catch (IOException e) {
                            Log.e(LOG_TAG, e.getMessage());
                        } catch (AuthenticatorException e) {
                            Log.e(LOG_TAG, e.getMessage());
                        }
                    }
                }, null);
            }
        });


        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AuthenticatorActivity.class));
            }
        });
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private HomeContract.Presenter mPresenter;

}
