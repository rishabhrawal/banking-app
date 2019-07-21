package com.revolut.account;


import com.revolut.exception.RevolutException;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class Account {

    @Id
    @GeneratedValue
    private long id;
    private LocalDateTime openingDate;
    private LocalDateTime closingDate;
    private boolean active;
    private boolean closed;
    private String name;
    private double balance;
    private AccountType accountType;

    protected final void setBalance(double balance) {
        this.balance = balance;
    }

    public abstract double debit(double amount) throws RevolutException;

    public abstract double credit(double amount) throws RevolutException;
}
