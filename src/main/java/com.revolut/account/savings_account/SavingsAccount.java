package com.revolut.account.savings_account;



import com.revolut.account.Account;
import com.revolut.exception.IllegalAccountStateException;
import com.revolut.exception.InsufficientBalanceException;
import com.revolut.exception.RevolutException;

import javax.persistence.Entity;
import java.math.BigDecimal;


@Entity
public class SavingsAccount extends Account {


    @Override
    public BigDecimal debit(BigDecimal amount) throws RevolutException {
        checkAccountValidity();
        BigDecimal balance = getBalance();
        if(balance==null){
            throw new InsufficientBalanceException(1, "Invalid Balance", null);
        }
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(1, "Insufficient Balance", null);
        }
        balance = balance.subtract(amount);
        setBalance(balance);
        return balance;
    }

    @Override
    public BigDecimal credit(BigDecimal amount) throws RevolutException {
        checkAccountValidity();
        BigDecimal balance = getBalance();
        if(balance==null){
            balance = new BigDecimal(0);
        }
        balance = balance.add(amount);
        setBalance(balance);
        return balance;
    }

    private boolean checkAccountValidity() throws RevolutException {
        if (getClosed()) {
            throw new IllegalAccountStateException(1, "Account is closed", null);
        }
        if (!getActive()) {
            throw new IllegalAccountStateException(1, "Account is Inactive", null);
        }
        return true;
    }
}
