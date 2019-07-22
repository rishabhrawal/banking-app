package com.revolut.account.transaction;


import com.revolut.common.JpaFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

public class SavingsTransactionService implements TransactionService {

    @Inject
    private JpaFactory jpaFactory;

    @Override
    public List<TransactionModel> getAllTransactions() {
        EntityManager em = jpaFactory.getEntityManager();
        TypedQuery<Transaction> query = em.createQuery("SELECT t FROM Transaction t", Transaction.class);
        return query.getResultList().stream().map(t -> mapTransactionToModel(t)).collect(Collectors.toList());
    }

    @Override
    public List<TransactionModel> getAllTransactionsForAccount(long accountId) {
        EntityManager em = jpaFactory.getEntityManager();
        TypedQuery<Transaction> query = em.createQuery("SELECT t FROM Transaction t", Transaction.class);
        return query.getResultList().stream().map(t -> mapTransactionToModel(t)).collect(Collectors.toList());
    }

    @Override
    public TransactionModel getTransaction(long transactionId) {
        EntityManager em = jpaFactory.getEntityManager();
        Transaction transaction = em.find(Transaction.class, transactionId);
        return mapTransactionToModel(transaction);
    }

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        EntityManager em = jpaFactory.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(transaction);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }

        return transaction;
    }

    public TransactionModel mapTransactionToModel(Transaction transaction) {
        TransactionModel transactionModel = new TransactionModel();
        if (transaction == null) {
            return transactionModel;
        }
        transactionModel.setId(transaction.getId());
        transactionModel.setTransactionType(transaction.getTransactionType());
        transactionModel.setAmount(transaction.getAmount());
        transactionModel.setDateTime(transaction.getDateTime());
        transactionModel.setDebitAccountId(transaction.getDebitAccountId());
        transactionModel.setCreditAccountId(transaction.getCreditAccountId());
        transactionModel.setStatus(transaction.getStatus());
        return transactionModel;
    }
}
