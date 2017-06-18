package com.jpventura.anypic.data.user;

import android.database.ContentObservable;
import android.support.annotation.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

public class UserRepository extends ContentObservable implements UserSource {

    private final UserSource mLocalSource;
    private final UserSource mRemoteSource;

    public UserRepository(@NonNull UserSource local, @NonNull UserSource remote) {
        super();
        mLocalSource = checkNotNull(local);
        mRemoteSource = checkNotNull(remote);
    }

}
