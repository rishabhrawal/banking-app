package com.revolut.account.savings;


import com.revolut.account.savings.SavingsAccount;
import com.revolut.account.savings.SavingsAccountService;
import com.revolut.exception.IllegalAccountStateException;
import com.revolut.exception.InsufficientBalanceException;
import com.revolut.exception.RevolutException;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SavingsAccountServiceTest {

    Logger logger = LoggerFactory.getLogger(SavingsAccountServiceTest.class);


    //@Mock
    //SavingsAccountRepository savingsAccountRepository;

    @Mock
    EntityManager entityManager;

    @Mock
    EntityTransaction entityTransaction;

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


    @Test(expected = IllegalAccountStateException.class)
    public void debitInactiveAccountException() throws RevolutException {
        savingsAccount.setActive(false);
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        savingsAccountService.debit(20.00, 1L);
    }

    @Test(expected = IllegalAccountStateException.class)
    public void debitClosedAccountException() throws RevolutException {
        savingsAccount.setClosed(true);
        savingsAccount.credit(50.00);
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        savingsAccountService.debit(20.00, 1L);
    }

    @Test(expected = InsufficientBalanceException.class)
    public void debitInsufficientBalanceException() throws RevolutException {
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        savingsAccountService.debit(51, 1L);
    }

    @Test
    public void debit() throws RevolutException {
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        double balance = savingsAccountService.debit(50.00, 1L);
        assertEquals(00.00, balance, 00);
    }

    @Test(expected = IllegalAccountStateException.class)
    public void creditInactiveAccountException() throws RevolutException {
        savingsAccount.setActive(false);
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        savingsAccountService.credit(20.00, 1L);
    }

    @Test(expected = IllegalAccountStateException.class)
    public void creditClosedAccountException() throws RevolutException {
        savingsAccount.setClosed(true);
        savingsAccount.credit(0);
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        savingsAccountService.credit(20.00, 1L);
    }

    @Test
    public void credit() throws RevolutException {
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        double balance = savingsAccountService.credit(20.00, 1L);
        assertEquals(70.00, balance, 0.0);
    }


    @Test(expected = InsufficientBalanceException.class)
    public void transferInsufficientBalance() throws RevolutException {
        SavingsAccount savingsAccount2 = new SavingsAccount();
        savingsAccount2.setActive(true);
        savingsAccount2.setClosed(false);
//        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        when(entityManager.find(SavingsAccount.class, 2L)).thenReturn(savingsAccount2);
        boolean result = savingsAccountService.transfer(51.00, 1L, 2L);
    }

    @Test
    public void transfer() throws RevolutException {
        SavingsAccount savingsAccount2 = new SavingsAccount();
        savingsAccount2.setActive(true);
        savingsAccount2.setClosed(false);
        savingsAccount2.credit(11.0);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(entityManager.find(SavingsAccount.class, 1L)).thenReturn(savingsAccount);
        when(entityManager.find(SavingsAccount.class, 2L)).thenReturn(savingsAccount2);
        boolean result = savingsAccountService.transfer(50.00, 1L, 2L);
        assertEquals(0.0, savingsAccount.getBalance(), 0.0);
        assertEquals(61.0, savingsAccount2.getBalance(), 0.0);
    }
}