package com.revolut.webapi;


import com.revolut.account.AccountModel;
import com.revolut.account.AccountService;
import com.revolut.exception.RevolutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Path("api/v1/account")
public class SavingsAccountResource {

    Logger logger = LoggerFactory.getLogger(SavingsAccountResource.class);

    @Inject
    AccountService accountService;


    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AccountModel createAccount(AccountModel accountModel) throws RevolutException {
        return accountService.create(accountModel);
    }

    @GET()
    @Path("/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AccountModel getAccountDetails(@PathParam("accountId") Long accountId) throws RevolutException, ExecutionException {
        logger.info("Account Id: "+accountId);
        return accountService.getAccountDetails(accountId);

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

    /*@GET()
    @Path("/balance")
    @Produces(MediaType.APPLICATION_JSON)
    public double getAccountBalance(@QueryParam("accountId") Long accountId) {
        return accountService.getBalance(accountId);
    }*/

    @POST
    @Path("/debit")
    @Produces(MediaType.APPLICATION_JSON)
    public double debitAccount(Double amount, Long accountId) throws RevolutException, ExecutionException {
        return accountService.debit(amount, accountId);
    }

    @POST
    @Path("/credit")
    @Produces(MediaType.APPLICATION_JSON)
    public double creditAccount(Double amount, Long accountId) throws RevolutException, ExecutionException {
        return accountService.credit(amount, accountId);
    }

    @POST
    @Path("/transfer")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean transfer(Double amount, Long fromAccountId, Long toAccountId) throws RevolutException, ExecutionException {
        return accountService.transfer(amount, fromAccountId, toAccountId);
    }


}
