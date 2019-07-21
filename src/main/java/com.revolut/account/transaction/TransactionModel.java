package com.revolut.account.transaction;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionModel {
    Long id;
    TransactionType transactionType;
    Double amount;
    Long debitAccountId;
    Long creditAccountId;
    LocalDateTime dateTime;
    Boolean status;
}
