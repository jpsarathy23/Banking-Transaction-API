package com.revolut.transaction_api.strategies;

import com.revolut.transaction_api.interfaces.TransactionStrategy;
import com.revolut.transaction_api.models.Account;
import com.revolut.transaction_api.models.Transaction;
import com.revolut.transaction_api.models.TransactionState;

public class SynchronisedTransactionStrategy implements TransactionStrategy {
    public void runTransaction(Transaction transaction) {
        Account fromAccount = transaction.getFromAccount();
        Account toAccount = transaction.getToAccount();
        Account account1 = fromAccount, account2 = toAccount;
        if (account1.getAccountId() >= account2.getAccountId()) {
            account1 = toAccount;
            account2 = fromAccount;
        }
        synchronized (account1) {
            synchronized (account2) {
                transaction.setTransactionState(TransactionState.RUNNING);
                if (fromAccount.getBalance() < transaction.getAmount()) {
                    transaction.setTransactionState(TransactionState.FAILED);
                    return;
                }
                fromAccount.setBalance(fromAccount.getBalance() - transaction.getAmount());
                toAccount.setBalance(toAccount.getBalance() + transaction.getAmount());
                transaction.setTransactionState(TransactionState.DONE);
            }
        }
    }
}
