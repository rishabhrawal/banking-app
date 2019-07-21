package com.revolut.account;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountModel {
    public AccountModel(){}
    private Long accountNumber;
    private String name;
    private AccountType accountType;
    private BigDecimal balance;
    private LocalDateTime openingDate;
    private Boolean active;
    private Boolean closed;
}
