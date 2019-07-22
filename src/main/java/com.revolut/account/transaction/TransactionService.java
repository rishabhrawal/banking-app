package com.revolut.account.transaction;

import java.util.List;

/**
 * Class to display the transactional data in the system
 * Transactional data cannot be modified once created
 */
public interface TransactionService {
    /**
     *
     * @return all the transactions in the system
     */
    List<TransactionModel> getAllTransactions();

    /**
     *
     * @param accountId Id of the account for which the transactions are needed
     * @return List of all the transactions for a given AccountId
     */
    List<TransactionModel> getAllTransactionsForAccount(long accountId);

    /**
     *
     * @param transactionId Id of the transaction
     * @return Transaction Model
     */
    TransactionModel getTransaction(long transactionId);

    /**
     *
     * @param transaction Databse Entity
     * @return Model Object
     */
    TransactionModel mapTransactionToModel(Transaction transaction);
}
