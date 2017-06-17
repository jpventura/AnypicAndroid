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

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import timber.log.Timber;

public class Tree extends Timber.Tree {

    public Tree(@NonNull final Context context) {
        super();
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable throwable) {
        if (Log.VERBOSE == priority) return;
    }

}
