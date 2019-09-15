package com.revolut.transaction_api.interfaces;

import com.revolut.transaction_api.models.Transaction;
import com.revolut.transaction_api.strategies.OptimisticConcurrencyControlStrategy;
import com.revolut.transaction_api.strategies.OptimisticTransactionStrategy;
import com.revolut.transaction_api.strategies.SynchronisedTransactionStrategy;

public interface TransactionStrategy {
    void runTransaction(Transaction transaction);
    static TransactionStrategy optimisticTransactionStrategy() {
        return new OptimisticTransactionStrategy();
    }
    static TransactionStrategy synchronisedTransactionStrategy() {
        return new SynchronisedTransactionStrategy();
    }
    static TransactionStrategy optimisticConcurrencyControlStrategy() {
        return new OptimisticConcurrencyControlStrategy();
    }
}
