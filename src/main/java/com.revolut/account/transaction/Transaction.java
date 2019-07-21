package com.revolut.account.transaction;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class Transaction {

    public Transaction() {
    }

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
    private Double amount;
    private LocalDateTime dateTime;
    private Boolean status;
    private String description;

}
