package com.revolut.transaction_api;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.revolut.transaction_api.helpers.AccountManager;
import com.revolut.transaction_api.helpers.TransactionManager;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;

@RunWith(ConcurrentTestRunner.class)
public class ParallelObjectCreationTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        server = TransactionApi.startServer();
        Client c = ClientBuilder.newClient();
        target = c.target(TransactionApi.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
        assertEquals(10, AccountManager.getLastAccountId());
        assertEquals(10, TransactionManager.getLastTransactionId());
    }

    @Test
    @ThreadCount(10)
    public void testAccountCreation() {
        Form form = new Form();
        form.param("name", "account");
        form.param("balance", "1000");
        String responseMsg = target.path("accounts").request()
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        System.out.println("ACCOUNT" + responseMsg);
        assertEquals("account", AccountManager.getAccount(1).getName());
    }

    @Test
    @ThreadCount(10)
    public void testTransactionCreation() {
        while(AccountManager.getTotalAccounts() < 2);
        Form form = new Form();
        form.param("from", "1");
        form.param("to", "2");
        form.param("amount", "100");
        String responseMsg = target.path("transactions").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        System.out.println("TRANSACTION" + responseMsg);

    }


}
