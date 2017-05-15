package com.jpventura.anypic;

import android.accounts.Account;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;

public interface AccountSource {

    Task<Boolean> addAccount(@NonNull Account account, @NonNull String password, @NonNull Bundle userdata);

    Task<String> getAuthToken(@NonNull final Account account, @NonNull Bundle options);

    boolean hasAccount(@NonNull Account account);

    Task<Boolean> removeAccount(@NonNull final Account account);

}
