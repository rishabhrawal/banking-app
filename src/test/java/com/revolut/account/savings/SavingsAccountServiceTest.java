package com.revolut.account.savings;


import com.revolut.lock.SavingsLockCache;
import com.revolut.common.JpaFactory;
import com.revolut.exception.IllegalAccountStateException;
import com.revolut.exception.InsufficientBalanceException;
import com.revolut.exception.RevolutException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SavingsAccountServiceTest {

    Logger logger = LoggerFactory.getLogger(SavingsAccountServiceTest.class);


    @Mock
    JpaFactory jpaFactory;

    @Mock
    SavingsLockCache lockCache;

    @InjectMocks
    SavingsAccountService savingsAccountService;

    private  SavingsAccount savingsAccount1;
    private  SavingsAccount savingsAccount2;


    @BeforeClass
    public static void  setupOnce() throws Exception{
    }

    @Before
    public void setUp() throws Exception {
        EntityManager entityManager = mock(EntityManager.class);
        when(jpaFactory.getEntityManager()).thenReturn(entityManager);
        when(lockCache.getLockForAccount(any())).thenReturn(new ReentrantReadWriteLock());

        savingsAccount1 = new SavingsAccount();
        savingsAccount1.setId(1L);
        savingsAccount1.setName("acc1");
        savingsAccount1.setActive(true);
        savingsAccount1.setClosed(false);
        savingsAccount1.credit(50.00);
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount1);
        savingsAccount2 = new SavingsAccount();
        savingsAccount1.setId(2L);
        savingsAccount1.setName("acc2");
        savingsAccount2.setActive(true);
        savingsAccount2.setClosed(false);
        when(entityManager.find(SavingsAccount.class, 2L)).thenReturn(savingsAccount2);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Ignore
    @Test(expected = RevolutException.class)
    public void createAccountWithInvalidData() {
    }

    @Test
    public void createSuccess() {
    }

    @Test
    public void getAccountDetails() {
    }

    /*@Test(expected = IllegalAccountStateException.class)
    public void closeAlreadyClosedAccount() {
    }*/

    @Test
    public void getBalance() throws RevolutException, ExecutionException {
        double balance = savingsAccountService.getBalance(1L);
        assertEquals(50.0, balance, 0.0);
    }

    @Test
    public void getBalanceInactiveAccount() throws RevolutException, ExecutionException {
        savingsAccount1.setActive(false);
        double balance = savingsAccountService.getBalance(1L);
        assertEquals(50.0, balance, 0.0);
    }

    @Test
    public void getBalanceClosedAccount() throws RevolutException, ExecutionException {
        savingsAccount1.setClosed(true);
        double balance = savingsAccountService.getBalance(1L);
        assertEquals(50.0, balance, 0.0);
    }


    @Test(expected = IllegalAccountStateException.class)
    public void debitInactiveAccountException() throws RevolutException, ExecutionException {
        savingsAccount1.setActive(false);
        savingsAccountService.debit(20.00, 1L);
    }

    @Test(expected = IllegalAccountStateException.class)
    public void debitClosedAccountException() throws RevolutException, ExecutionException {
        savingsAccount1.setClosed(true);
        savingsAccountService.debit(20.00, 1L);
    }

    @Test(expected = InsufficientBalanceException.class)
    public void debitInsufficientBalanceException() throws RevolutException, ExecutionException {
        savingsAccountService.debit(51, 1L);
    }

    @Test
    public void debit() throws RevolutException, ExecutionException {
        double balance = savingsAccountService.debit(50.00, 1L);
        assertEquals(00.00, balance, 00);
    }

    @Test(expected = IllegalAccountStateException.class)
    public void creditInactiveAccountException() throws RevolutException, ExecutionException {
        savingsAccount1.setActive(false);
        savingsAccountService.credit(20.00, 1L);
    }

    @Test(expected = IllegalAccountStateException.class)
    public void creditClosedAccountException() throws RevolutException, ExecutionException {
        savingsAccount1.setClosed(true);
        savingsAccount1.credit(0);
        savingsAccountService.credit(20.00, 1L);
    }

    @Test
    public void credit() throws RevolutException, ExecutionException {
        double balance = savingsAccountService.credit(20.00, 1L);
        assertEquals(70.00, balance, 0.0);
    }


    @Test(expected = InsufficientBalanceException.class)
    public void transferInsufficientBalance() throws RevolutException, ExecutionException {
        boolean result = savingsAccountService.transfer(51.00, 1L, 2L);
    }

    @Test
    public void transfer() throws RevolutException, ExecutionException {
        savingsAccount2.credit(11.0);
        boolean result = savingsAccountService.transfer(50.00, 1L, 2L);
        assertEquals(0.0, savingsAccount1.getBalance(), 0.0);
        assertEquals(61.0, savingsAccount2.getBalance(), 0.0);
    }
}