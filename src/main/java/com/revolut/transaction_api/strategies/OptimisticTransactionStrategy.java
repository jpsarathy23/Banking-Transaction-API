package com.revolut.transaction_api.strategies;

import com.revolut.transaction_api.interfaces.TransactionStrategy;
import com.revolut.transaction_api.exceptions.InsufficientBalanceException;
import com.revolut.transaction_api.exceptions.InvalidKeyTransaction;
import com.revolut.transaction_api.models.Account;
import com.revolut.transaction_api.models.Transaction;
import com.revolut.transaction_api.models.TransactionState;

public class OptimisticTransactionStrategy implements TransactionStrategy {
    public void runTransaction(Transaction transaction) {
        Account fromAccount = transaction.getFromAccount();
        Account toAccount = transaction.getToAccount();
        try {
            transaction.setTransactionState(TransactionState.RUNNING);
            fromAccount.setTransaction(transaction);
            toAccount.setTransaction(transaction);
            fromAccount.debit(transaction);
            toAccount.credit(transaction);
            if (fromAccount.canCommit(transaction) && toAccount.canCommit(transaction)) {
                try {
                    fromAccount.commit(transaction);
                    toAccount.commit(transaction);
                    transaction.setTransactionState(TransactionState.DONE);
                } catch (InvalidKeyTransaction invalidKeyTransaction) {
                    rollbackTransaction(transaction);
                    transaction.setTransactionState(TransactionState.FAILED);
                }
            } else {
                rollbackTransaction(transaction);
                transaction.setTransactionState(TransactionState.ABORTED);
            }
        } catch (InvalidKeyTransaction invalidKeyTransaction) {
            rollbackTransaction(transaction);
            transaction.setTransactionState(TransactionState.ABORTED);
        } catch (InsufficientBalanceException e) {
            rollbackTransaction(transaction);
            transaction.setTransactionState(TransactionState.FAILED);
        }

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
