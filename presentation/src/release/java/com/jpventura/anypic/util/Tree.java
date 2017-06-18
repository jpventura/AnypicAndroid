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

package com.jpventura.anypic.util;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;

import com.facebook.appevents.AppEventsLogger;

import io.fabric.sdk.android.Fabric;

import timber.log.Timber;

public class Tree extends Timber.Tree {

    private final FirebaseAnalytics mFirebaseAnalytics;

    public Tree(@NonNull final Application application) {
        super();
        AppEventsLogger.activateApp(application);
        Fabric.with(application, new Answers());
        Fabric.with(application, new Crashlytics(), new CrashlyticsNdk());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(application);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable throwable) {
        if (isUserConnected()) {
            final FirebaseUser user = getUser();
            Crashlytics.setUserIdentifier(user.getUid());
            Crashlytics.setUserEmail(user.getEmail());
            Crashlytics.setUserName(user.getDisplayName());
        }

        switch (priority) {
            case Log.ERROR:
                Crashlytics.log(priority, tag, message);
                Crashlytics.logException(throwable);

                FirebaseCrash.logcat(priority, tag, message);
                FirebaseCrash.report(throwable);
                break;
        }
    }

    private static boolean isUserConnected() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        return (null != auth) && (auth.getCurrentUser() != null);
    }

    private static FirebaseUser getUser() throws IllegalStateException {
        try {
            return FirebaseAuth.getInstance().getCurrentUser();
        } catch (NullPointerException e) {
            throw new IllegalStateException(e);
        }
    }

}
