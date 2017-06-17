package com.jpventura.anypic.accounts;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {

        FirebaseAccountAuthenticator authenticator = new FirebaseAccountAuthenticator(this);
        return authenticator.getIBinder();
    }

}
