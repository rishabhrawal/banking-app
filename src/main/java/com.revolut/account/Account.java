package com.revolut.account;


import com.revolut.exception.RevolutException;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.ZonedDateTime;

@Data
@MappedSuperclass
public abstract class Account {

    @Id
    @GeneratedValue
    private long id;
    private ZonedDateTime openingDate; //jdbc 4.2 +
    private ZonedDateTime closingDate;
    private boolean active;
    private boolean closed;
    private String name;
    private double balance;

    protected final void setBalance(double balance) {
        this.balance = balance;
    }

    public abstract double debit(double amount) throws RevolutException;

    public abstract double credit(double amount) throws RevolutException;
}
