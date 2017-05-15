package com.jpventura.anypic;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.icu.util.TimeUnit;
import android.os.Handler;
import android.os.HandlerThread;
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

    private Context mContext;
    private AccountSource mRepository;
    private Account mAccount;
    private String mPassword;

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
        mAccount = new Account("maria@email.com", "com.jpventura.anypic");
        mPassword = "abc123";
        mRepository = AccountLocalSource.getInstance(InstrumentationRegistry.getTargetContext());

        Tasks.await(mRepository.removeAccount(mAccount));
    }

    @After
    public void tearDown() throws Exception {
        mRepository.removeAccount(mAccount);
    }

    @Test
    public void testAddAccount() throws Exception {
        final Account account = new Account("maria@email.com", "com.jpventura.anypic");
        Tasks.await(mRepository.removeAccount(account));
        assertTrue(Tasks.await(mRepository.addAccount(mAccount, mPassword, null)));
        assertTrue(mRepository.hasAccount(mAccount));
        assertTrue(Tasks.await(mRepository.removeAccount(account)));
    }

    @Test
    public void testGetAuthToken() throws Exception {
        final String authTokenType = "com.jpventura.anypic";
        final String password = "abc123";
        final Account account = new Account("maria@email.com", authTokenType);

        Tasks.await(mRepository.removeAccount(account));
        assertTrue(Tasks.await(mRepository.addAccount(mAccount, mPassword, null)));
        assertTrue(mRepository.hasAccount(mAccount));

        HandlerThread thread = new HandlerThread("oi");
        thread.start();

        Handler handler = new Handler(thread.getLooper());
        // assertNotNull(AccountManager.get(mContext).blockingGetAuthToken(account, authTokenType, true));
        // assertNotNull(AccountManager.get(mContext).getAuthToken(account, authTokenType, null, false, null, handler).getResult());

        assertTrue(Tasks.await(mRepository.removeAccount(account)));

//         assertNotNull(AccountManager.get(mContext).getAuthToken(account, authTokenType, null, false, null, null));
        // assertEquals("SmartMEI", Tasks.await(mRepository.getAuthToken(account, null)));
    }

    @Test
    public void testRemoveAccount() throws Exception {
        final Account account = new Account("maria@email.com", "com.jpventura.anypic");
        assertTrue(Tasks.await(mRepository.addAccount(mAccount, mPassword, null)));
        assertTrue(Tasks.await(mRepository.removeAccount(account)));
        assertTrue(!mRepository.hasAccount(mAccount));
    }

}
