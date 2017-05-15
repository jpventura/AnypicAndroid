package com.jpventura.anypic;

import android.accounts.Account;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AccountRepositoryTest {

    private AccountSource mRepository;
    private Account mAccount;
    private String mPassword;

    @Before
    public void setUp() throws Exception {
        mAccount = new Account("maria@email.com", "com.jpventura.anypic");
        mPassword = "abc123";
        mRepository = AccountLocalSource.getInstance(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void tearDown() throws Exception {
        mRepository.removeAccountExplicitly(mAccount);
    }

    @Test
    public void testAddAccountExplicitly() throws Exception {
        final Account account = new Account("maria@email.com", "com.jpventura.anypic");
        Tasks.await(mRepository.removeAccountExplicitly(account));
        assertTrue(mRepository.addAccountExplicitly(mAccount, mPassword, null));
        assertTrue(mRepository.hasAccount(mAccount));
        assertTrue(Tasks.await(mRepository.removeAccountExplicitly(account)));
    }

    @Test
    public void testRemoveAccountExplicitly() throws Exception {
        final Account account = new Account("maria@email.com", "com.jpventura.anypic");
        mRepository.addAccountExplicitly(mAccount, mPassword, null);
        assertTrue(Tasks.await(mRepository.removeAccountExplicitly(account)));
        assertTrue(!mRepository.hasAccount(mAccount));
    }

}
