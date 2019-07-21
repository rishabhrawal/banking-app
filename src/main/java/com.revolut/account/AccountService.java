package com.revolut.account;


import com.revolut.account.transaction.TransactionModel;
import com.revolut.exception.RevolutException;

import java.util.List;
import java.util.concurrent.ExecutionException;

//@Contract
public interface AccountService {
    AccountModel create(AccountModel accountModel) throws RevolutException, ExecutionException;

    List<AccountModel> getAllAccounts();

    AccountModel getAccountDetails(long accountId) throws RevolutException, ExecutionException;

    double getBalance(long accountId) throws ExecutionException;

    TransactionModel debit(TransactionModel transactionModel) throws RevolutException, ExecutionException;

    TransactionModel credit(TransactionModel transactionModel) throws RevolutException, ExecutionException;

    TransactionModel transfer(TransactionModel transactionModel) throws RevolutException, ExecutionException;

    boolean close(long accountId) throws ExecutionException;

    static void validateAmount(double amount) throws RevolutException {
        if (amount < 0.0) {
            throw new RevolutException(1, "Transaction amount cannot be negative", null);
        }
    }
}
