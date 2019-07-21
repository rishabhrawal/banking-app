package com.revolut.transaction;

import java.util.List;

/**
 * Class to display the transactional data in the system
 *
 */
public interface TransactionService {
    List<Transaction> getAllTransactions();
    Transaction getTransaction(long transactionId);
}
