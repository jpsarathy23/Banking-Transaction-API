package com.revolut.transaction_api.strategies;

import com.revolut.transaction_api.interfaces.TransactionStrategy;
import com.revolut.transaction_api.exceptions.InvalidKeyTransaction;
import com.revolut.transaction_api.models.Account;
import com.revolut.transaction_api.models.Transaction;
import com.revolut.transaction_api.models.TransactionState;

import java.time.Instant;
import java.util.Map;

public class OptimisticConcurrencyControlStrategy implements TransactionStrategy {
    public void runTransaction(Transaction transaction) {
        transaction.setTransactionState(TransactionState.RUNNING);
        Integer transactionId = transaction.getTransactionId();
        Account fromAccount = transaction.getFromAccount();
        fromAccount.transactionIdReadTimestampMap.put(transactionId, Instant.now().getEpochSecond());
        Account toAccount = transaction.getToAccount();
        toAccount.transactionIdReadTimestampMap.put(transactionId, Instant.now().getEpochSecond());
        double fromAccountBalance = fromAccount.getBalance() - transaction.getAmount();
        if (fromAccountBalance < 0.0) {
            transaction.setTransactionState(TransactionState.FAILED);
            return;
        }
        fromAccount.transactionIdWriteTimestampMap.put(transactionId, Instant.now().getEpochSecond());
        double toAccountBalance = toAccount.getBalance() + transaction.getAmount();
        synchronized ("VALIDATION") {
            long timestamp = Instant.now().getEpochSecond();
            long fromAccountReadConflictCount = getConflictCount(fromAccount.transactionIdReadTimestampMap, transactionId, timestamp);
            long toAccountReadConflictCount = getConflictCount(toAccount.transactionIdReadTimestampMap, transactionId, timestamp);
            long fromAccountWriteConflictCount = getConflictCount(fromAccount.transactionIdWriteTimestampMap, transactionId, timestamp);
            long toAccountWriteConflictCount = getConflictCount(toAccount.transactionIdWriteTimestampMap, transactionId, timestamp);
            if (fromAccountReadConflictCount == 0 && toAccountReadConflictCount == 0 && fromAccountWriteConflictCount == 0 && toAccountWriteConflictCount== 0) {
                fromAccount.setBalance(fromAccountBalance);
                toAccount.setBalance(toAccountBalance);
                transaction.setTransactionState(TransactionState.DONE);
            } else {
                transaction.setTransactionState(TransactionState.ABORTED);
            }
            fromAccount.transactionIdReadTimestampMap.remove(transactionId);
            toAccount.transactionIdReadTimestampMap.remove(transactionId);
        }
    }
    
    public long getConflictCount(Map<Integer, Long> transactionIdTimestampMap, Integer transactionId, long timestamp) {
        return transactionIdTimestampMap.entrySet().stream()
                .filter(entry ->
                        entry.getValue() < timestamp)
                .filter(entry ->
                        entry.getKey() != transactionId
                ).count();
    }
    public void rollbackTransaction(Transaction transaction) {
        try {
            transaction.getFromAccount().abort(transaction);
            transaction.getToAccount().abort(transaction);
        } catch (InvalidKeyTransaction invalidKeyTransaction) {
            transaction.setTransactionState(TransactionState.FAILED);
        }
    }
}
