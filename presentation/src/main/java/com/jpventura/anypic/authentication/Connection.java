package com.jpventura.anypic.authentication;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Observable;
import java.util.Observer;

class Connection extends Observable implements FirebaseAuth.AuthStateListener {

    private final FirebaseAuth mAuth;

    Connection() {
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        setChanged();
        notifyObservers(firebaseAuth);
    }

    @Override
    public synchronized void addObserver(Observer observer) {
        synchronized (this) {
            super.addObserver(observer);
            if (countObservers() > 0) {
                mAuth.addAuthStateListener(this);
            }
        }
    }

    @Override
    public synchronized void deleteObserver(Observer observer) {
        synchronized (this) {
            super.deleteObserver(observer);
            if (countObservers() == 0) {
                mAuth.removeAuthStateListener(this);
            }
        }
    }

}
