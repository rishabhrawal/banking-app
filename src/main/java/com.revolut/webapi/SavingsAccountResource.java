package com.revolut.webapi;


import com.revolut.account.AccountModel;
import com.revolut.account.AccountService;
import com.revolut.account.savings.SavingsAccount;
import com.revolut.account.savings.SavingsAccountService;
import com.revolut.exception.RevolutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("/api/v1/account")
public class SavingsAccountResource {

    Logger logger = LoggerFactory.getLogger(SavingsAccountResource.class);

    //@Inject
    //AccountService accountService;

    SavingsAccountService accountService = new SavingsAccountService();


    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AccountModel createAccount(AccountModel accountModel) throws RevolutException {
        return accountService.create(accountModel);
    }

    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public List<SavingsAccount> getAllAccounts() throws RevolutException {
        logger.info("Get all accounts");
        return accountService.getAllAccounts();

    }

    @GET()
    @Path("/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AccountModel getAccountDetails(@PathParam("accountId") Long accountId) throws RevolutException {
        logger.info("Account Id: "+accountId);
        return accountService.getAccountDetails(accountId);

    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean closeAccount(@QueryParam("accountId") Long accountId) {
        return accountService.close(accountId);
    }

    /*@GET()
    @Path("/balance")
    @Produces(MediaType.APPLICATION_JSON)
    public double getAccountBalance(@QueryParam("accountId") Long accountId) {
        return accountService.getBalance(accountId);
    }

    @POST
    @Path("/debit")
    @Produces(MediaType.APPLICATION_JSON)
    public double debitAccount(Double amount, Long accountId) throws RevolutException {
        return accountService.debit(amount, accountId);
    }

    @POST
    @Path("/credit")
    @Produces(MediaType.APPLICATION_JSON)
    public double creditAccount(Double amount, Long accountId) throws RevolutException {
        return accountService.credit(amount, accountId);
    }*/


}
