package com.revolut.webapi;

import com.revolut.account.transaction.TransactionModel;
import com.revolut.account.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("api/v1/accounts/savings/transactions")
public class TransactionResource {

    private static final Logger logger = LoggerFactory.getLogger(TransactionResource.class);


    @Inject
    private TransactionService transactionService;

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


}
