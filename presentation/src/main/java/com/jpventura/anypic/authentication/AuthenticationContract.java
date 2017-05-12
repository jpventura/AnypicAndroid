package com.jpventura.anypic.authentication;

import com.jpventura.anypic.BasePresenter;
import com.jpventura.anypic.BaseView;

public class AuthenticationContract {

    interface Presenter extends BasePresenter {
    }

    interface View extends BaseView<Presenter> {

        @Override
        void setPresenter(Presenter presenter);

    }

}
