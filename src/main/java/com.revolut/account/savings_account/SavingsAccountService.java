package com.revolut.account.savings_account;


import com.revolut.account.AccountModel;
import com.revolut.account.AccountService;
import com.revolut.account.AccountType;
import com.revolut.account.transaction.*;
import com.revolut.lock.SavingsLockCache;
import com.revolut.exception.InsufficientBalanceException;
import com.revolut.exception.RevolutException;
import com.revolut.common.JpaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;


public class SavingsAccountService implements AccountService {

    Logger logger = LoggerFactory.getLogger(SavingsAccountService.class);

    @Inject
    JpaFactory jpaFactory;

    @Inject
    SavingsLockCache lockCache;

    @Inject
    TransactionService transactionService;

    /**
     * @param accountModel
     * @return
     */
    @Override
    public AccountModel create(AccountModel accountModel) throws RevolutException, ExecutionException {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setName(accountModel.getName());
        savingsAccount.setClosed(false);
        savingsAccount.setActive(true);
        EntityManager em = jpaFactory.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(savingsAccount);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }

        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setCreditAccountId(savingsAccount.getId());
        transactionModel.setAmount(accountModel.getBalance());
        credit(transactionModel);

        accountModel.setAccountNumber(savingsAccount.getId());
        accountModel.setAccountType(savingsAccount.getAccountType());
        return accountModel;
    }

    @Override
    public List<AccountModel> getAllAccounts() {
        EntityManager em = jpaFactory.getEntityManager();
        TypedQuery<SavingsAccount> query = em.createQuery("SELECT a FROM SavingsAccount a", SavingsAccount.class);
        return query.getResultList().stream().map(a -> mapAccountToModel(a)).collect(Collectors.toList());
    }

    /**
     * @param accountId
     * @return
     */
    @Override
    public AccountModel getAccountDetails(long accountId) throws RevolutException, ExecutionException {
        logger.debug("get account details for : " + accountId);
        Lock lock = lockCache.getLockForAccount(accountId).readLock();
        SavingsAccount savingsAccount = null;
        try {
            lock.lock();
            EntityManager em = jpaFactory.getEntityManager();
            savingsAccount = em.find(SavingsAccount.class, accountId);
        } finally {
            lock.unlock();
        }
        if (savingsAccount == null) {
            throw new RevolutException(1, "Invalid account id", null);
        }
        AccountModel accountModel = new AccountModel();
        accountModel.setAccountType(AccountType.SAVINGS);
        accountModel.setAccountNumber(savingsAccount.getId());
        accountModel.setBalance(savingsAccount.getBalance());
        accountModel.setName(savingsAccount.getName());
        accountModel.setActive(savingsAccount.isActive());
        accountModel.setClosed(savingsAccount.isClosed());
        accountModel.setOpeningDate(savingsAccount.getOpeningDate());
        return accountModel;
        //return Optional.ofNullable(accountModel);
    }


    /**
     * @param accountId
     * @return
     */
    @Override
    public synchronized double getBalance(long accountId) throws ExecutionException {
        Lock lock = lockCache.getLockForAccount(accountId).readLock();
        SavingsAccount savingsAccount = null;
        EntityManager em = jpaFactory.getEntityManager();
        try {
            lock.lock();
            savingsAccount = em.find(SavingsAccount.class, accountId);
        } finally {
            lock.unlock();
        }
        return savingsAccount.getBalance();
    }

    @Override
    public TransactionModel debit(TransactionModel transactionModel) throws RevolutException, ExecutionException {
        long accountId = transactionModel.getDebitAccountId();
        double amount = transactionModel.getAmount();
        AccountService.validateAmount(amount);
        Lock lock = lockCache.getLockForAccount(accountId).writeLock();
        SavingsAccount savingsAccount = null;

        Transaction transaction = new Transaction(TransactionType.DEBIT);
        transaction.setAmount(amount);
        transaction.setDebitAccountId(accountId);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setStatus(false);
        try {
            lock.lock();
            EntityManager em = jpaFactory.getEntityManager();
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(transaction);
                //em.detach(transaction);
                savingsAccount = em.find(SavingsAccount.class, accountId);
                if (savingsAccount == null) {
                    throw new RevolutException(1, "Account not found", null);
                }
                savingsAccount.debit(amount);
                em.persist(savingsAccount);
                transaction.setStatus(true);
                em.persist(transaction);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) tx.rollback();
                throw e;
            } finally {
                em.close();
            }

        } finally {
            lock.unlock();
        }
        return transactionService.mapTransactionToModel(transaction);
    }

    @Override
    public TransactionModel credit(TransactionModel transactionModel) throws RevolutException, ExecutionException {

        long accountId = transactionModel.getCreditAccountId();
        double amount = transactionModel.getAmount();

        AccountService.validateAmount(amount);
        SavingsAccount savingsAccount = null;

        Transaction transaction = new Transaction(TransactionType.CREDIT);
        transaction.setAmount(amount);
        transaction.setCreditAccountId(accountId);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setStatus(false);

        Lock lock = lockCache.getLockForAccount(accountId).writeLock();
        try {
            lock.lock();

            EntityManager em = jpaFactory.getEntityManager();
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                savingsAccount = em.find(SavingsAccount.class, accountId);
                if (savingsAccount == null) {
                    throw new RevolutException(1, "Account not found", null);
                }

                savingsAccount.credit(amount);
                em.persist(savingsAccount);
                transaction.setStatus(true);
                em.persist(transaction);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) tx.rollback();
                throw e;
            } finally {
                em.close();
            }
        } finally {
            lock.unlock();
        }
        return transactionService.mapTransactionToModel(transaction);
    }


    @Override
    public TransactionModel transfer(TransactionModel transactionModel) throws RevolutException, ExecutionException {

        long debitAccountId = transactionModel.getDebitAccountId();
        long creditAccountId = transactionModel.getCreditAccountId();
        double amount = transactionModel.getAmount();
        AccountService.validateAmount(amount);


        Transaction transaction = new Transaction(TransactionType.TRANSFER);
        transaction.setAmount(amount);
        transaction.setDebitAccountId(debitAccountId);
        transaction.setCreditAccountId(creditAccountId);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setStatus(false);

        AccountService.validateAmount(amount);
        Lock lock1 = lockCache.getLockForAccount(debitAccountId).writeLock();
        Lock lock2 = lockCache.getLockForAccount(creditAccountId).writeLock();
        try {
            //maintain locking order to avoid deadlock
            if (debitAccountId > creditAccountId) {
                lock1.lock();
                lock2.lock();
            } else {
                lock2.lock();
                lock1.lock();
            }
            EntityManager em = jpaFactory.getEntityManager();
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                SavingsAccount debitAccount = em.find(SavingsAccount.class, debitAccountId);
                if (debitAccount == null) {
                    throw new RevolutException(1, "Account(" + debitAccountId + ") not found", null);
                }
                SavingsAccount creditAccount = em.find(SavingsAccount.class, creditAccountId);
                if (creditAccount == null) {
                    throw new RevolutException(1, "Account(" + creditAccountId + ") not found", null);
                }

                // check account 1 has enough balance
                if (debitAccount.getBalance() < amount) {
                    throw new InsufficientBalanceException(1, "Insufficient Balance in  the account", null);
                }

                debitAccount.debit(amount);
                creditAccount.credit(amount);
                em.persist(debitAccount);
                em.persist(creditAccount);
                transaction.setStatus(true);
                em.persist(transaction);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) tx.rollback();
                throw e;
            } finally {
                em.close();
            }

        } finally {
            lock1.unlock();
            lock2.unlock();
        }
        return transactionService.mapTransactionToModel(transaction); //mapAccountToModel(savingsAccount);
    }

    /**
     * @param accountId
     * @return
     */
    @Override
    public synchronized boolean close(long accountId) throws ExecutionException {
        Lock lock = lockCache.getLockForAccount(accountId).writeLock();
        SavingsAccount savingsAccount = null;
        try {
            lock.lock();
            EntityManager em = jpaFactory.getEntityManager();
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                //better than find, as returns proxy
                savingsAccount = em.getReference(SavingsAccount.class, accountId);
                savingsAccount.setClosed(true);
                em.persist(savingsAccount);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) tx.rollback();
                throw e;
            } finally {
                em.close();
            }
        } finally {
            lock.unlock();
        }
        return true;
    }

    private AccountModel mapAccountToModel(SavingsAccount account) {
        AccountModel accountModel = new AccountModel();
        if (account == null) {
            return accountModel;
        }
        accountModel.setAccountType(account.getAccountType());
        accountModel.setActive(account.isActive());
        accountModel.setClosed(account.isClosed());
        accountModel.setAccountNumber(account.getId());
        accountModel.setBalance(account.getBalance());
        accountModel.setName(account.getName());
        return accountModel;
    }

}
