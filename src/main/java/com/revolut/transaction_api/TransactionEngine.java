package com.revolut.transaction_api;

import com.revolut.transaction_api.helpers.TransactionManager;
import com.revolut.transaction_api.interfaces.TransactionStrategy;
import com.revolut.transaction_api.models.Transaction;
import com.revolut.transaction_api.models.TransactionState;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TransactionEngine {
    Executor executor;
    TransactionStrategy transactionStrategy;

    public TransactionEngine(int numThreads, TransactionStrategy transactionStrategy) {
        executor = Executors.newFixedThreadPool(numThreads);
        this.transactionStrategy = transactionStrategy;
    }

    public void executeTransactions(boolean stopOnceCompleted) {
        while(true) {
            List<Transaction> pendingTransactions = TransactionManager.getTransactionList().stream().filter(transaction ->
                transaction.getTransactionState() == TransactionState.ABORTED ||
                    transaction.getTransactionState() == TransactionState.QUEUED
            ).collect(Collectors.toList());
            if (stopOnceCompleted && pendingTransactions.size() == 0) {
                break;
            } else {
                System.out.println("PENDING TRANSACTIONS IN QUEUE " + pendingTransactions.size());
                pendingTransactions.forEach(transaction ->
                    executor.execute(() -> transactionStrategy.runTransaction(transaction))
                    //transactionStrategy.runTransaction(transaction)
                );
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
