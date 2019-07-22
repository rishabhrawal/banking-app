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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static com.revolut.common.Constants.*;


public class SavingsAccountService implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(SavingsAccountService.class);

    @Inject
    private JpaFactory jpaFactory;

    @Inject
    private SavingsLockCache lockCache;

    @Inject
    private TransactionService transactionService;

    @Inject
    SavingsAccountDao savingsAccountDao;

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
        savingsAccount.setOpeningDate(LocalDateTime.now());
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
        List<SavingsAccount> savingsAccountList = savingsAccountDao.getAllAccounts();
        if (savingsAccountList != null) {
            return savingsAccountList.stream().map(a -> mapAccountToModel(a)).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
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
           /* Optional<SavingsAccount> value = savingsAccountDao.getAccount(accountId);
            if (!value.isPresent()) {
                throw new RevolutException(1, "Invalid account id", null);
            }*/
            EntityManager em = jpaFactory.getEntityManager();

            try {
                savingsAccount = em.find(SavingsAccount.class, accountId);
            } finally {
                em.close();
            }
            if (savingsAccount==null) {
                throw new RevolutException(INVALID_ACCOUNT_ID_CODE, INVALID_ACCOUNT_ID_TEXT, null);
            }

        } finally {
            lock.unlock();
        }
        AccountModel accountModel = new AccountModel();
        accountModel.setAccountType(AccountType.SAVINGS);
        accountModel.setAccountNumber(savingsAccount.getId());
        accountModel.setBalance(savingsAccount.getBalance());
        accountModel.setName(savingsAccount.getName());
        accountModel.setActive(savingsAccount.getActive());
        accountModel.setClosed(savingsAccount.getClosed());
        accountModel.setOpeningDate(savingsAccount.getOpeningDate());
        return accountModel;
    }


    @Override
    public TransactionModel debit(TransactionModel transactionModel) throws RevolutException, ExecutionException {
        long accountId = transactionModel.getDebitAccountId();
        BigDecimal amount = transactionModel.getAmount();
        AccountService.validateAmount(amount);
        Lock lock = lockCache.getLockForAccount(accountId).writeLock();
        SavingsAccount savingsAccount = null;
        Transaction transaction = new Transaction(TransactionType.DEBIT);
        transaction.setAmount(amount);
        transaction.setDebitAccountId(accountId);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setStatus(false);
        transaction = transactionService.saveTransaction(transaction);
        try {
            lock.lock();
            EntityManager em = jpaFactory.getEntityManager();
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                savingsAccount = em.find(SavingsAccount.class, accountId);
                if (savingsAccount == null) {
                    throw new RevolutException(ACCOUNT_NOT_FOUND_CODE, ACCOUNT_NOT_FOUND_TEXT, null);
                }
                savingsAccount.debit(amount);
                transaction.setStatus(true);
                em.merge(transaction);
                tx.commit();
            } catch (Exception e) {
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
        BigDecimal amount = transactionModel.getAmount();

        AccountService.validateAmount(amount);
        SavingsAccount savingsAccount = null;

        Transaction transaction = new Transaction(TransactionType.CREDIT);
        transaction.setAmount(amount);
        transaction.setCreditAccountId(accountId);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setStatus(false);
        transaction = transactionService.saveTransaction(transaction);
        Lock lock = lockCache.getLockForAccount(accountId).writeLock();
        try {
            lock.lock();

            EntityManager em = jpaFactory.getEntityManager();
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                savingsAccount = em.find(SavingsAccount.class, accountId);
                if (savingsAccount == null) {
                    throw new RevolutException(ACCOUNT_NOT_FOUND_CODE, ACCOUNT_NOT_FOUND_TEXT, null);
                }

                savingsAccount.credit(amount);
                transaction.setStatus(true);
                em.merge(transaction);
                tx.commit();
            } catch (Exception e) {
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
        BigDecimal amount = transactionModel.getAmount();
        AccountService.validateAmount(amount);


        Transaction transaction = new Transaction(TransactionType.TRANSFER);
        transaction.setAmount(amount);
        transaction.setDebitAccountId(debitAccountId);
        transaction.setCreditAccountId(creditAccountId);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setStatus(false);
        transaction = transactionService.saveTransaction(transaction);

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
                    throw new RevolutException(ACCOUNT_NOT_FOUND_CODE, ACCOUNT_NOT_FOUND_TEXT + ":"+debitAccountId , null);
                }
                SavingsAccount creditAccount = em.find(SavingsAccount.class, creditAccountId);
                if (creditAccount == null) {
                    throw new RevolutException(ACCOUNT_NOT_FOUND_CODE, ACCOUNT_NOT_FOUND_TEXT+":"+ creditAccountId, null);
                }

                // check debit account has enough balance
                if (debitAccount.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientBalanceException(INVALID_BALANCE_CODE, INVALID_BALANCE_TEXT, null);
                }

                debitAccount.debit(amount);
                creditAccount.credit(amount);
                transaction.setStatus(true);
                em.merge(transaction);
                tx.commit();
            } catch (Exception e) {
                if (tx != null && tx.isActive()) tx.rollback();
                throw e;
            } finally {
                em.close();
            }

        } finally {
            lock1.unlock();
            lock2.unlock();
        }
        return transactionService.mapTransactionToModel(transaction);
    }

    /**
     * @param accountId
     * @return
     */
    @Override
    public AccountModel close(long accountId) throws ExecutionException {
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
            } catch (Exception e) {
                if (tx != null && tx.isActive()) tx.rollback();
                throw e;
            } finally {
                em.close();
            }
        } finally {
            lock.unlock();
        }
        return mapAccountToModel(savingsAccount);
    }

    private AccountModel mapAccountToModel(SavingsAccount account) {
        AccountModel accountModel = new AccountModel();
        if (account == null) {
            return accountModel;
        }
        accountModel.setAccountType(account.getAccountType());
        accountModel.setActive(account.getActive());
        accountModel.setClosed(account.getClosed());
        accountModel.setAccountNumber(account.getId());
        accountModel.setBalance(account.getBalance());
        accountModel.setName(account.getName());
        return accountModel;
    }

}
