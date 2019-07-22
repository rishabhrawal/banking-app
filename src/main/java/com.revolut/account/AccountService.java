package com.revolut.account;


import com.revolut.account.transaction.TransactionModel;
import com.revolut.exception.RevolutException;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.revolut.common.Constants.*;

public interface AccountService {


    /**
     *
     * @param accountModel
     * @return
     * @throws RevolutException  when trying to credit the initial amountw
     * @throws ExecutionException  hen unable to get the cached lock
     */
    AccountModel create(AccountModel accountModel) throws RevolutException, ExecutionException;

    /**
     *
     * @return List of all the Accounts
     */
    List<AccountModel> getAllAccounts();

    /**
     *
     * @param accountId
     * @return Account corresponding to the AccountID
     * @throws RevolutException
     * @throws ExecutionException
     */
    AccountModel getAccountDetails(long accountId) throws RevolutException, ExecutionException;

    /**
     *
     * @param transactionModel Common transaction object representing a debit
     * @return Details of the completed transaction
     * @throws RevolutException  when debit is not possible on account
     * @throws ExecutionException when unable to get the cached lock
     */
    TransactionModel debit(TransactionModel transactionModel) throws RevolutException, ExecutionException;

    /**
     *
     * @param transactionModel
     * @return
     * @throws RevolutException when credit to the account is not possible
     * @throws ExecutionException when unable to get the cached lock
     */
    TransactionModel credit(TransactionModel transactionModel) throws RevolutException, ExecutionException;

    /**
     *
     * @param transactionModel
     * @return
     * @throws RevolutException when transfer between accounts is not possible
     * @throws ExecutionException when unable to get the cached lock
     */
    TransactionModel transfer(TransactionModel transactionModel) throws RevolutException, ExecutionException;

    /**
     *
     * @param accountId
     * @return closed AccountModel
     * @throws ExecutionException when unable to get the cached lock
     */
    AccountModel close(long accountId) throws ExecutionException;

    /**
     *
     * @param amount
     * @throws RevolutException validation for the transaction amount
     */
    static void validateAmount(BigDecimal amount) throws RevolutException {
        if(amount==null){
            throw new RevolutException(INVALID_TRANSACTION_AMOUNT_CODE, INVALID_TRANSACTION_AMOUNT_TEXT, null);
        }
        if (amount.doubleValue() < 0.0) {
            throw new RevolutException(TRANSACTION_AMOUNT_CANNOT_BE_NEGATIVE_CODE, TRANSACTION_AMOUNT_CANNOT_BE_NEGATIVE_TEXT, null);
        }
    }
}
