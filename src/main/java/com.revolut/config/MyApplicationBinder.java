package com.revolut.config;

import com.revolut.account.AccountService;
import com.revolut.account.transaction.SavingsTransactionService;
import com.revolut.account.transaction.TransactionService;
import com.revolut.lock.SavingsLockCache;
import com.revolut.account.savings_account.SavingsAccountService;
import com.revolut.common.JpaFactory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class MyApplicationBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(SavingsAccountService.class).to(SavingsAccountService.class);
        bind(SavingsAccountService.class).to(AccountService.class);
        bind(SavingsTransactionService.class).to(TransactionService.class);
        bind(JpaFactory.class).to(JpaFactory.class);
        bind(SavingsLockCache.class).to(SavingsLockCache.class);
    }
}