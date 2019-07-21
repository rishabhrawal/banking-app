package com.revolut.account.transaction;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Transaction {

    public Transaction() {}

    public Transaction(TransactionType transactionType){
        this.dateTime = LocalDateTime.now();
        this.transactionType = transactionType;
    }

    @Id
    @GeneratedValue
    private Long id;
    private TransactionType transactionType;
    private Long debitAccountId;
    private Long creditAccountId;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private Boolean status;
    private String description;

}
