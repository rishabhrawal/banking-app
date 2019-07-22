package com.revolut.webapi;

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

@Path("api/v1/accounts/savings/transactions")
public class TransactionResource {

    private static final Logger logger = LoggerFactory.getLogger(TransactionResource.class);


    @Inject
    private TransactionService transactionService;

    @Inject
    private AccountService accountService;

    @GET()
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TransactionModel> getAllTransactions() {
        return transactionService.getAllTransactions();

    }


    @GET()
    @Path("/{transactionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public  TransactionModel getTransaction(@PathParam("transactionId") Long transactionId) {
        return transactionService.getTransaction(transactionId);

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
