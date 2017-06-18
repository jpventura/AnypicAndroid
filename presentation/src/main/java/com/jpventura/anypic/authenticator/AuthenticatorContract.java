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

import jpventura.app.BasePresenter;
import jpventura.app.BaseView;

interface AuthenticatorContract {

    interface Presenter extends BasePresenter {

        void signIn(@NonNull final String provider);

        void result(int requestCode, int resultCode, Intent result);

    }

    interface View extends BaseView<Presenter> {
    }

}
