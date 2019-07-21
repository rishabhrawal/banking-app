package com.revolut.webapi;


import com.revolut.account.AccountModel;
import com.revolut.account.AccountService;
import com.revolut.account.transaction.TransactionModel;
import com.revolut.account.transaction.TransactionService;
import com.revolut.exception.RevolutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Path("api/v1/accounts/savings")
public class SavingsAccountResource {

    Logger logger = LoggerFactory.getLogger(SavingsAccountResource.class);

    @Inject
    AccountService accountService;


    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AccountModel createAccount(AccountModel accountModel) throws RevolutException, ExecutionException {
        return accountService.create(accountModel);
    }

    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public List<AccountModel> getAllAccounts() throws RevolutException {
        logger.info("Get all accounts");
        return accountService.getAllAccounts();

    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean closeAccount(@QueryParam("accountId") Long accountId) throws ExecutionException {
        return accountService.close(accountId);
    }

    @GET()
    @Path("/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AccountModel getAccountDetails(@PathParam("accountId") Long accountId) throws RevolutException, ExecutionException {
        logger.info("Account Id: " + accountId);
        return accountService.getAccountDetails(accountId);

    }


    @POST
    @Path("/credit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionModel creditAccount( TransactionModel transactionModel) throws RevolutException, ExecutionException {
        logger.info("Credit, TransactionModel: " + transactionModel);
        return accountService.credit(transactionModel);
    }

    @POST
    @Path("/debit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionModel debitAccount(TransactionModel transactionModel) throws RevolutException, ExecutionException {
        logger.info("Debit, TransactionModel: " + transactionModel);
        return accountService.debit(transactionModel);
    }

    @POST
    @Path("/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionModel transfer(TransactionModel transactionModel) throws RevolutException, ExecutionException {
        logger.info("Transfer, TransactionModel: " + transactionModel);
        return accountService.transfer(transactionModel);
    }


}
