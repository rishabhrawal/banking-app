package com.revolut.account.savings_account;


import com.revolut.account.AccountModel;
import com.revolut.account.AccountService;
import com.revolut.account.AccountType;
import com.revolut.lock.SavingsLockCache;
import com.revolut.account.transaction.Transaction;
import com.revolut.account.transaction.TransactionType;
import com.revolut.exception.InsufficientBalanceException;
import com.revolut.exception.RevolutException;
import com.revolut.common.JpaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
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

    /**
     * @param accountModel
     * @return
     */
    @Override
    public AccountModel create(AccountModel accountModel) throws RevolutException {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setName(accountModel.getName());
        savingsAccount.setClosed(false);
        savingsAccount.setActive(true);
        savingsAccount.credit(0.0);
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

        accountModel.setAccountNumber(savingsAccount.getId());
        accountModel.setAccountType(savingsAccount.getAccountType());
        return accountModel;
    }

    @Override
    public List<AccountModel> getAllAccounts() {
        EntityManager em = jpaFactory.getEntityManager();
        TypedQuery<SavingsAccount> query = em.createQuery("SELECT a FROM SavingsAccount a", SavingsAccount.class);
        return  query.getResultList().stream().map(a->mapAccountToModel(a)).collect(Collectors.toList());
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
    public double debit(double amount, long accountId) throws RevolutException, ExecutionException {
        AccountService.validateAmount(amount);
        Lock lock = lockCache.getLockForAccount(accountId).writeLock();
        SavingsAccount savingsAccount;
        try {
            lock.lock();
            EntityManager em = jpaFactory.getEntityManager();
            Transaction transaction = new Transaction(TransactionType.DEBIT);
            transaction.setAmount(amount);
            transaction.setDebitAccount(accountId);
            em.persist(transaction);
            em.detach(transaction);
            EntityTransaction tx = em.getTransaction();
            /*try {
                tx.begin();*/
                savingsAccount = em.find(SavingsAccount.class, accountId);
                if (savingsAccount == null) {
                    throw new RevolutException(1, "Account not found", null);
                }
                savingsAccount.debit(amount);
                //em.persist(savingsAccount);
                transaction.setStatus(true);
                em.persist(transaction);
                /*tx.commit();
            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) tx.rollback();
                throw e;
            } finally {
                em.close();
            }*/

        } finally {
            lock.unlock();
        }
        return savingsAccount.getBalance();
    }

    @Override
    public double credit(double amount, long accountId) throws RevolutException, ExecutionException {
        AccountService.validateAmount(amount);
        SavingsAccount savingsAccount = null;
        Lock lock = lockCache.getLockForAccount(accountId).writeLock();
        try {
            lock.lock();
            EntityManager em = jpaFactory.getEntityManager();
            savingsAccount = em.find(SavingsAccount.class, accountId);
            if (savingsAccount == null) {
                throw new RevolutException(1, "Account not found", null);
            }

            Transaction transaction = new Transaction(TransactionType.CREDIT);
            transaction.setAmount(amount);
            transaction.setCreditAccount(accountId);
            transaction.setStatus(false);

            savingsAccount.credit(amount);
            em.persist(savingsAccount);
            transaction.setStatus(true);
            em.persist(transaction);
        } finally {
            lock.unlock();
        }
        return savingsAccount.getBalance();
    }


    @Override
    public boolean transfer(double amount, long accountId1, long accountId2) throws RevolutException, ExecutionException {
        AccountService.validateAmount(amount);
        Lock lock1 = lockCache.getLockForAccount(accountId1).writeLock();
        Lock lock2 = lockCache.getLockForAccount(accountId2).writeLock();
        try {
            //maintain locking order to avoid deadlock
            if (accountId1 > accountId2) {
                lock1.lock();
                lock2.lock();
            } else {
                lock2.lock();
                lock1.lock();
            }
            EntityManager em = jpaFactory.getEntityManager();
            SavingsAccount account1 = em.find(SavingsAccount.class, accountId1);
            if (account1 == null) {
                throw new RevolutException(1, "Account(" + accountId1 + ") not found", null);
            }
            SavingsAccount account2 = em.find(SavingsAccount.class, accountId2);
            if (account2 == null) {
                throw new RevolutException(1, "Account(" + accountId2 + ") not found", null);
            }

            // check account 1 has enough balance
            if (account1.getBalance() < amount) {
                throw new InsufficientBalanceException(1, "Insufficient Balance in  the account", null);
            }

            //this entry is repeated for debit credit if the transaction  is successful
            Transaction transaction = new Transaction(TransactionType.TRANSFER);
            transaction.setAmount(amount);
            transaction.setDebitAccount(accountId1);
            transaction.setCreditAccount(accountId2);
            transaction.setStatus(false);
            em.persist(transaction);
            account1.debit(amount);
            account2.credit(amount);
            em.persist(account1);
            em.persist(account2);
            transaction.setStatus(true);
            em.persist(transaction);

        } finally {
            lock1.unlock();
            lock2.unlock();
        }
        return true;
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
        accountModel.setAccountType(account.getAccountType());
        accountModel.setActive(account.isActive());
        accountModel.setClosed(account.isClosed());
        accountModel.setAccountNumber(account.getId());
        accountModel.setBalance(account.getBalance());
        accountModel.setName(account.getName());
        return accountModel;
    }


}
