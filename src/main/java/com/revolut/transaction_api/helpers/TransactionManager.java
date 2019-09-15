package com.revolut.transaction_api.helpers;

import com.revolut.transaction_api.models.Transaction;
import com.revolut.transaction_api.models.TransactionState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TransactionManager {
    private static AtomicInteger transactionId = new AtomicInteger(0);
    private static ConcurrentHashMap<Integer, Transaction> transactionMap = new ConcurrentHashMap<>();

    public static Transaction createTransaction(String fromAccountId, String toAccountId, double amount) {
        int id = transactionId.incrementAndGet();
        Transaction transaction = new Transaction(id, fromAccountId, toAccountId, amount, TransactionState.QUEUED);
        transactionMap.put(id, transaction);
        //System.out.println("Transaction created");
        return transaction;
    }

    public static Transaction getTransaction(int id) {
        return transactionMap.get(id);
    }

    public static List<Transaction> getTransactionList() {
        return new ArrayList<>(transactionMap.values());
    }

    public static int getLastTransactionId() {
        return transactionId.get();
    }

    public static Map<TransactionState, Long> getTransactionStatusMap() {
        return transactionMap.values().stream().collect(Collectors.groupingBy(Transaction::getTransactionState, Collectors.counting()));
    }
}
