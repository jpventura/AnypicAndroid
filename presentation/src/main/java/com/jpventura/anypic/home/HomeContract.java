package com.jpventura.anypic.home;

import android.accounts.Account;
import android.os.Bundle;

import com.google.android.gms.tasks.Task;
import com.jpventura.anypic.BasePresenter;
import com.jpventura.anypic.BaseView;

interface HomeContract {

    interface Presenter extends BasePresenter {

        Task<Bundle> getAuthToken(Account account);

    }

    interface View extends BaseView<Presenter> {

        @Override
        void setPresenter(Presenter presenter);

    }

}
