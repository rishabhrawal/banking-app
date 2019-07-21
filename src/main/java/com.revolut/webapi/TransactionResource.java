package com.revolut.webapi;

import com.revolut.account.AccountService;
import com.revolut.account.transaction.TransactionModel;
import com.revolut.account.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("api/v1/accounts/savings/transactions")
public class TransactionResource {

    Logger logger = LoggerFactory.getLogger(TransactionResource.class);


    @Inject
    TransactionService transactionService;

    @GET()
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TransactionModel> getAllTransactions() {
        return transactionService.getAllTransactions();

    }
}
