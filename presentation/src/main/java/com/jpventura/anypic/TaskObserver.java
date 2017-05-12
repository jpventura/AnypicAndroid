package com.jpventura.anypic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public interface TaskObserver<T> extends OnFailureListener, OnSuccessListener<T> {

    @Override
    void onFailure(@NonNull Exception e);

    @Override
    void onSuccess(T t);

}
