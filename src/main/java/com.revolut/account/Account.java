package com.revolut.account;


import com.revolut.exception.RevolutException;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class Account {

    @Id
    @GeneratedValue
    private Long id;
    private LocalDateTime openingDate;
    private LocalDateTime closingDate;
    private Boolean active;
    private Boolean closed;
    private String name;
    private BigDecimal balance;
    private AccountType accountType;

    protected final void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public abstract BigDecimal debit(BigDecimal amount) throws RevolutException;

    public abstract BigDecimal credit(BigDecimal amount) throws RevolutException;
}
