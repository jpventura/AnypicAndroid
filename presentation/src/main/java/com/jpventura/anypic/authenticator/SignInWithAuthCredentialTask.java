package com.jpventura.anypic.authenticator;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

class SignInWithAuthCredentialTask implements Continuation<AuthCredential, Task<AuthResult>> {

    @Override
    public Task<AuthResult> then(@NonNull Task<AuthCredential> task) throws Exception {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.signInWithCredential(task.getResult());
    }

}
