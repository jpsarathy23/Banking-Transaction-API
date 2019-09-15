package com.revolut.transaction_api.services;

import com.revolut.transaction_api.helpers.AccountManager;
import com.revolut.transaction_api.helpers.TransactionManager;
import com.revolut.transaction_api.models.Transaction;
import com.revolut.transaction_api.models.TransactionState;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("transactions")
public class TransactionService {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Transaction createTransaction(@FormParam("from") String fromAccountId,
                                         @FormParam("to") String toAccountId,
                                         @FormParam("amount") double amount) {
        if (Integer.valueOf(fromAccountId) <= AccountManager.getTotalAccounts() &&
            Integer.valueOf(toAccountId) <= AccountManager.getTotalAccounts()) {
            return TransactionManager.createTransaction(fromAccountId, toAccountId, amount);
        } else {
            return null;
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Transaction getTransaction(@PathParam("id") int id) {
        return TransactionManager.getTransaction(id);
    }

    @GET
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<TransactionState, Long> getTransactionStatus() {
        return TransactionManager.getTransactionStatusMap();
    }
}
