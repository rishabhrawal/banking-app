package com.revolut.account.savings;


import com.revolut.account.Account;
import com.revolut.account.AccountModel;
import com.revolut.account.AccountService;
import com.revolut.account.AccountType;
import com.revolut.account.lock.LockManager;
import com.revolut.account.transaction.Transaction;
import com.revolut.account.transaction.TransactionType;
import com.revolut.common.AppUtility;
import com.revolut.exception.InsufficientBalanceException;
import com.revolut.exception.RevolutException;
import com.revolut.common.JpaUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;

//@Service
public class SavingsAccountService implements AccountService {

    Logger logger = LoggerFactory.getLogger(SavingsAccountService.class);

    //EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory( "accountService" );
    //EntityManager em = entityManagerFactory.createEntityManager();

    //@PersistenceContext(unitName = "accountService")
    //EntityManager em;

    EntityManager em = JpaUtility.getEntityManager();

    LockManager accountLock = new LockManager();


    /**
     * @param accountModel
     * @return
     */
    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public AccountModel create(AccountModel accountModel) throws RevolutException {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setName(accountModel.getName());
        savingsAccount.setClosed(false);
        savingsAccount.setActive(true);
        savingsAccount.credit(0.0);
        //savingsAccount.setOpeningDate(ZonedDateTime.now(AppUtility.LOCAL_ZONE_ID));
        EntityManager em = JpaUtility.getEntityManager();
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
        return accountModel;
    }

    @Override
    public List<SavingsAccount> getAllAccounts() {
        Query query = em.createQuery("SELECT a FROM SavingsAccount a");
        return (List<SavingsAccount>) query.getResultList();
    }

    /**
     * @param accountId
     * @return
     */
    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public AccountModel getAccountDetails(long accountId) throws RevolutException {
        logger.debug("get account details for : " + accountId);
        logger.debug("Enitity manager: " + em);
        Lock lock = accountLock.getLockForAccount(accountId).readLock();
        SavingsAccount savingsAccount = null;
        try {
            lock.lock();
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
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public synchronized double getBalance(long accountId) {
        Lock lock = accountLock.getLockForAccount(accountId).readLock();
        SavingsAccount savingsAccount = null;
        try {
            lock.lock();
            savingsAccount = em.find(SavingsAccount.class, accountId);
        } finally {
            lock.unlock();
        }
        return savingsAccount.getBalance();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public double debit(double amount, long accountId) throws RevolutException {
        /*if(amount < 0){
            throw new RevolutException(1, "Debit amount cannot be negative", null);
        }*/
        AccountService.validateAmount(amount);
        Lock lock = accountLock.getLockForAccount(accountId).writeLock();
        SavingsAccount savingsAccount;
        try {
            lock.lock();
            savingsAccount = em.find(SavingsAccount.class, accountId);
            if (savingsAccount == null) {
                throw new RevolutException(1, "Account not found", null);
            }

            Transaction transaction = new Transaction(TransactionType.DEBIT);
            transaction.setAmount(amount);
            transaction.setDebitAccount(accountId);

            em.persist(transaction);
            savingsAccount.debit(amount);
            em.persist(savingsAccount);
            transaction.setStatus(true);
            em.persist(transaction);
        } finally {
            lock.unlock();
        }
        return savingsAccount.getBalance();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public double credit(double amount, long accountId) throws RevolutException {
        /*if(amount < 0){
            throw new RevolutException(1, "Credit amount cannot be negative", null);
        }*/
        AccountService.validateAmount(amount);
        SavingsAccount savingsAccount = null;
        Lock lock = accountLock.getLockForAccount(accountId).writeLock();
        try {
            lock.lock();
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
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean transfer(double amount, long accountId1, long accountId2) throws RevolutException {

        Lock lock1 = accountLock.getLockForAccount(accountId1).writeLock();
        Lock lock2 = accountLock.getLockForAccount(accountId2).writeLock();
        try {
            //lock1.lock();
            //lock2.lock();
            //maintain locking order to avoid deadlock
            if (accountId1 > accountId2) {
                lock1.lock();
                lock2.lock();
            } else {
                lock2.lock();
                lock1.lock();
            }
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
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(account1);
                em.persist(account2);
                tx.commit();
            } finally {
                tx.rollback();
            }
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
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public synchronized boolean close(long accountId) {
        Lock lock = accountLock.getLockForAccount(accountId).writeLock();
        SavingsAccount savingsAccount = null;
        try {
            lock.lock();
            //better than find, as returns proxy
            savingsAccount = em.getReference(SavingsAccount.class, accountId);
            savingsAccount.setClosed(true);
            em.persist(savingsAccount);
        } finally {
            lock.unlock();
        }
        return true;
    }

    private AccountModel mapAccountToModel(SavingsAccount account) {
        AccountModel accountModel = new AccountModel();
        accountModel.setAccountNumber(account.getId());
        accountModel.setBalance(account.getBalance());
        accountModel.setName(account.getName());
        return accountModel;
    }


}
