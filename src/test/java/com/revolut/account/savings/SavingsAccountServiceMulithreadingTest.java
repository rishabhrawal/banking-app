package com.revolut.account.savings;



import com.revolut.exception.RevolutException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/*
@RunWith(MockitoJUnitRunner.class)
public class SavingsAccountServiceMulithreadingTest {

    Logger logger = LoggerFactory.getLogger(SavingsAccountServiceMulithreadingTest.class);


    //@Mock
    //SavingsAccountRepository savingsAccountRepository;

    @Mock
    EntityManager entityManager;

    @InjectMocks
    SavingsAccountService savingsAccountService;

    SavingsAccount savingsAccount;

    @Before
    public void setUp() throws Exception {
        savingsAccount = new SavingsAccount();
        savingsAccount.setActive(true);
        savingsAccount.setClosed(false);
        savingsAccount.credit(50.00);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getBalance() throws RevolutException {
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        double balance = savingsAccountService.getBalance(1L);
        assertEquals(50.0, balance, 0.0);
    }

    @Test
    public void getBalanceInactiveAccount() throws RevolutException {
        savingsAccount.setActive(false);
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        double balance = savingsAccountService.getBalance(1L);
        assertEquals(50.0, balance, 0.0);
    }

    @Test
    public void getBalanceClosedAccount() throws RevolutException {
        savingsAccount.setClosed(true);
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        double balance = savingsAccountService.getBalance(1L);
        assertEquals(50.0, balance, 0.0);
    }


    @Test
    public void debit() throws RevolutException {
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        double balance = savingsAccountService.debit(50.00, 1L);
        assertEquals(00.00, balance, 00);
    }

    @Test
    public void credit() throws RevolutException {
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        double balance = savingsAccountService.credit(20.00, 1L);
        assertEquals(70.00, balance, 0.0);
    }

    @Test
    public void transfer() throws RevolutException {
        SavingsAccount savingsAccount2 = new SavingsAccount();
        savingsAccount2.setActive(true);
        savingsAccount2.setClosed(false);
        savingsAccount2.credit(11.0);
*/
/*        when(entityManager.find(SavingsAccount.class, 1L)).thenAnswer(new Answer<SavingsAccount>() {
            @Override
            public SavingsAccount answer(InvocationOnMock invocationOnMock) throws InterruptedException {
                Thread.sleep(5000);
                return savingsAccount;
            }
        });*//*

        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        when(entityManager.find(SavingsAccount.class, 2L)).thenReturn(savingsAccount2);
        boolean resultA = savingsAccountService.transfer(50.00, 1L, 2L);
        boolean resultB = savingsAccountService.transfer(50.00, 2L, 1L);
        //assertEquals(0.0, savingsAccount.getBalance(),0.0);
        //assertEquals(61.0, savingsAccount2.getBalance(), 0.0);
    }
}*/
