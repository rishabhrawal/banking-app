package com.revolut.account;


import com.revolut.account.transaction.TransactionModel;
import com.revolut.exception.RevolutException;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

//@Contract
public interface AccountService {
    AccountModel create(AccountModel accountModel) throws RevolutException, ExecutionException;

    List<AccountModel> getAllAccounts();

    AccountModel getAccountDetails(long accountId) throws RevolutException, ExecutionException;

    TransactionModel debit(TransactionModel transactionModel) throws RevolutException, ExecutionException;

    TransactionModel credit(TransactionModel transactionModel) throws RevolutException, ExecutionException;

    TransactionModel transfer(TransactionModel transactionModel) throws RevolutException, ExecutionException;

    AccountModel close(long accountId) throws ExecutionException;

    static void validateAmount(BigDecimal amount) throws RevolutException {
        if(amount==null){
            throw new RevolutException(1, "Invalid transaction amount", null);
        }
        if (amount.doubleValue() < 0.0) {
            throw new RevolutException(1, "Transaction amount cannot be negative", null);
        }
    }
}
