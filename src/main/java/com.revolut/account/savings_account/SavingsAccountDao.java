package com.revolut.account.savings_account;

import com.revolut.common.JpaFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class SavingsAccountDao {

    @Inject
    private JpaFactory jpaFactory;


    List<SavingsAccount> getAllAccounts() {
        EntityManager em = jpaFactory.getEntityManager();
        List<SavingsAccount> savingsAccountList = null;
        try {
            TypedQuery<SavingsAccount> query = em.createQuery("SELECT a FROM SavingsAccount a", SavingsAccount.class);
            savingsAccountList = query.getResultList();
        } finally {
            em.close();
        }
        return savingsAccountList;
    }

    public SavingsAccount saveSavingsAccount(SavingsAccount savingsAccount) {
        EntityManager em = jpaFactory.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(savingsAccount);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
        return savingsAccount;
    }

    public Optional<SavingsAccount> getAccount(long accountId) {

        EntityManager em = jpaFactory.getEntityManager();

        SavingsAccount savingsAccount = null;
        try {
            em.find(SavingsAccount.class, accountId);
        } finally {
            em.close();
        }
        return Optional.ofNullable(savingsAccount);
    }
}
