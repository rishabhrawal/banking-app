package com.revolut.account.transaction;

import java.util.List;

/**
 * Class to display the transactional data in the system
 * Transactional data cannot be modified once created
 */
public interface TransactionService {
    List<TransactionModel> getAllTransactions();
    List<TransactionModel> getAllTransactionsForAccount(long accountId);
    TransactionModel getTransaction(long transactionId);
    TransactionModel mapTransactionToModel(Transaction transaction);
}
