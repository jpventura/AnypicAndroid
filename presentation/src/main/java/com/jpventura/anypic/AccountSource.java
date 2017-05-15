package com.jpventura.anypic;

import android.accounts.Account;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;

public interface AccountSource {

    boolean addAccountExplicitly(@NonNull Account account, @NonNull String password, @NonNull Bundle userdata);

    boolean hasAccount(@NonNull Account account);

    Task<Boolean> removeAccountExplicitly(@NonNull final Account account);

}
