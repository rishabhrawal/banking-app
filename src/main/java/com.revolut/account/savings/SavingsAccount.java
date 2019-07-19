package com.revolut.account.savings;



import com.revolut.account.Account;
import com.revolut.exception.IllegalAccountStateException;
import com.revolut.exception.InsufficientBalanceException;
import com.revolut.exception.RevolutException;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
public class SavingsAccount extends Account {

    @Override
    public double debit(double amount) throws RevolutException {
        checkAccountValidity();
        BigDecimal bigAmount = new BigDecimal(amount);
        BigDecimal bigBalance = new BigDecimal(getBalance());
        if (bigBalance.compareTo(bigAmount) < 0) {
            throw new InsufficientBalanceException(1, "Insufficient Balance", null);
        }
        double balance = bigBalance.subtract(bigAmount).doubleValue();
        setBalance(balance);
        return balance;
    }

    @Override
    public double credit(double amount) throws RevolutException {
        checkAccountValidity();
        BigDecimal bigAmount = new BigDecimal(amount);
        BigDecimal bigBalance = new BigDecimal(getBalance());
        double balance = bigBalance.add(bigAmount).doubleValue();
        setBalance(balance);
        return balance;
    }

    private boolean checkAccountValidity() throws RevolutException {
        if (isClosed()) {
            throw new IllegalAccountStateException(1, "Account is closed", null);
        }
        if (!isActive()) {
            throw new IllegalAccountStateException(1, "Account is Inactive", null);
        }
        return true;
    }
}
