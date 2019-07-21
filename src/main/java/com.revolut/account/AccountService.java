package com.revolut.account;


import com.revolut.exception.RevolutException;

import java.util.List;
import java.util.concurrent.ExecutionException;

//@Contract
public interface AccountService {
    AccountModel create(AccountModel accountModel) throws RevolutException;

    List<AccountModel> getAllAccounts();

    AccountModel getAccountDetails(long accountId) throws RevolutException, ExecutionException;

    double getBalance(long accountId) throws ExecutionException;

    double debit(double amount, long accountId) throws RevolutException, ExecutionException;

    double credit(double amount, long accountId) throws RevolutException, ExecutionException;

    boolean close(long accountId) throws ExecutionException;

    boolean transfer(double amount, long account1, long account2) throws RevolutException, ExecutionException;

    static void validateAmount(double amount) throws RevolutException {
        if (amount < 0.0) {
            throw new RevolutException(1, "Transaction amount cannot be negative", null);
        }
    }
}
