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
import android.util.SparseArray;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.stetho.inspector.console.CLog;
import com.facebook.stetho.inspector.protocol.module.Console.MessageLevel;
import com.facebook.stetho.inspector.protocol.module.Console.MessageSource;

import timber.log.Timber;

public class Tree extends Timber.DebugTree {

    private static final SparseArray<MessageLevel> MESSAGE_LEVEL_BY_PRIORITY;
    static {
        MESSAGE_LEVEL_BY_PRIORITY = new SparseArray<>();
        MESSAGE_LEVEL_BY_PRIORITY.put(Log.ASSERT, MessageLevel.ERROR);
        MESSAGE_LEVEL_BY_PRIORITY.put(Log.DEBUG, MessageLevel.DEBUG);
        MESSAGE_LEVEL_BY_PRIORITY.put(Log.ERROR, MessageLevel.ERROR);
        MESSAGE_LEVEL_BY_PRIORITY.put(Log.INFO, MessageLevel.LOG);
        MESSAGE_LEVEL_BY_PRIORITY.put(Log.VERBOSE, MessageLevel.DEBUG);
        MESSAGE_LEVEL_BY_PRIORITY.put(Log.WARN, MessageLevel.WARNING);
    }

    public Tree(@NonNull final Application application) {
        super();
        AppEventsLogger.activateApp(application);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable throwable) {
        super.log(priority, tag, message, throwable);
        console(priority, message);
    }

    private void console(int priority, String message) {
        CLog.writeToConsole(MESSAGE_LEVEL_BY_PRIORITY.get(priority), MessageSource.OTHER, message);
    }

}
