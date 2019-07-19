package com.revolut.account;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class AccountModel {
    private Long accountNumber;
    private String name;
    private AccountType accountType;
    private Double balance;
    private ZonedDateTime openingDate;
    private Boolean active;
    private Boolean closed;
}
