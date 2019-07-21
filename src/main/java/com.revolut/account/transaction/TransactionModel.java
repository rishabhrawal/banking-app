package com.revolut.account.transaction;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionModel {
    private Long id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private Long debitAccountId;
    private Long creditAccountId;
    private LocalDateTime dateTime;
    private Boolean status;
}
