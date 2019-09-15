package com.revolut.transaction_api.services;


import com.revolut.transaction_api.helpers.AccountManager;
import com.revolut.transaction_api.models.Account;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("accounts")
public class AccountService {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Account createAccount(@FormParam("name") String name, @FormParam("balance") double balance) {
        if(balance == 0.0) balance = 1000.0;
        return AccountManager.createAccount(name,  balance);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("id") int id) {
        return AccountManager.getAccount(id);
    }
}
