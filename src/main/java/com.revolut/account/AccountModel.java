package com.revolut.account;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountModel {
    private Long accountNumber;
    private String name;
    private AccountType accountType;
    private Double balance;
    private LocalDateTime openingDate;
    private Boolean active;
    private Boolean closed;
}
