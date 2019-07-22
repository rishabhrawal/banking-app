package com.revolut.account.savings_account;



import com.revolut.account.Account;
import com.revolut.exception.IllegalAccountStateException;
import com.revolut.exception.InsufficientBalanceException;
import com.revolut.exception.RevolutException;

import javax.persistence.Entity;
import java.math.BigDecimal;

import static com.revolut.common.Constants.*;


@Entity
public class SavingsAccount extends Account {


    @Override
    public BigDecimal debit(BigDecimal amount) throws RevolutException {
        checkAccountValidity();
        BigDecimal balance = getBalance();
        if(balance==null){
            throw new InsufficientBalanceException(INVALID_BALANCE_CODE, INVALID_BALANCE_TEXT, null);
        }
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(INSUFFICIENT_BALANCE_CODE, INSUFFICIENT_BALANCE_TEXT, null);
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
            throw new IllegalAccountStateException(ACCOUNT_IS_CLOSED_CODE, ACCOUNT_IS_CLOSED_TEXT, null);
        }
        if (!getActive()) {
            throw new IllegalAccountStateException(ACCOUNT_IS_INACTIVE_CODE, ACCOUNT_IS_INACTIVE_TEXT, null);
        }
        return true;
    }
}
