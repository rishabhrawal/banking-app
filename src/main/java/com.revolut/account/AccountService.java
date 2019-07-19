package com.revolut.account;


import com.revolut.account.savings.SavingsAccount;
import com.revolut.exception.RevolutException;

import java.util.List;
//import org.jvnet.hk2.annotations.Contract;

//@Contract
public interface AccountService {
    AccountModel create(AccountModel accountModel) throws RevolutException;

    List<SavingsAccount> getAllAccounts();

    AccountModel getAccountDetails(long accountId) throws RevolutException;

    double getBalance(long accountId);

    double debit(double amount, long accountId) throws RevolutException;

    double credit(double amount, long accountId) throws RevolutException;

    boolean close(long accountId);

    boolean transfer(double amount, long account1, long account2) throws RevolutException;

    static void validateAmount(double amount) throws RevolutException {
        if (amount < 0.0) {
            throw new RevolutException(1, "Transaction amount cannot be negative", null);
        }
    }
}
