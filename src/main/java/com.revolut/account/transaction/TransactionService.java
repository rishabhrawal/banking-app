package com.revolut.account.transaction;

import java.util.List;

/**
 * Class to display the transactional data in the system
 * Transactional data cannot be modified once created
 */
public interface TransactionService {
    List<Transaction> getAllTransactions();
    Transaction getTransaction(long transactionId);
}
