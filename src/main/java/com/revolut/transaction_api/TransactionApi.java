package com.revolut.transaction_api;

import com.revolut.transaction_api.interfaces.TransactionStrategy;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class TransactionApi {
    public static final String BASE_URI = "http://localhost:8080/";
    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("com.revolut.transaction_api");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("CHOOSE STRATEGY\n1.SYNCHRONISED BLOCK(2PL) - DEFAULT\n2.OPTIMISTIC(ACCOUNT TRANSACTION LOCKING)\n3.OPTIMISTIC CONCURRENCY CONTROL\n");
        int option = System.in.read();
        TransactionStrategy transactionStrategy = TransactionStrategy.synchronisedTransactionStrategy();
        switch (option) {
            case '2':
                transactionStrategy = TransactionStrategy.optimisticTransactionStrategy();
                break;
            case '3':
                transactionStrategy = TransactionStrategy.optimisticConcurrencyControlStrategy();

        }
        final HttpServer server = startServer();
        System.out.println("Jersey app started");
        System.out.println("Strategy - " + transactionStrategy.getClass().getSimpleName());
        TransactionEngine transactionEngine = new TransactionEngine(5, transactionStrategy);
        transactionEngine.executeTransactions(false);
    }
}

