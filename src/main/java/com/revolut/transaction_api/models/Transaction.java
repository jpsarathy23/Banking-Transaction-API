package com.revolut.transaction_api.models;


import com.revolut.transaction_api.helpers.AccountManager;

public class Transaction {
    private int transactionId;
    private Account fromAccount;
    private Account toAccount;
    private double amount;
    private TransactionState transactionState;

    public Transaction(int transactionId, String fromAccountId, String toAccountId, double amount, TransactionState transactionState) {
        this.transactionId = transactionId;
        this.fromAccount = AccountManager.getAccount(new Integer(fromAccountId));
        this.toAccount = AccountManager.getAccount(new Integer(toAccountId));
        this.amount = amount;
        this.transactionState = transactionState;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }

    public TransactionState getTransactionState() {
        return transactionState;
    }

    public void setTransactionState(TransactionState transactionState) {
        this.transactionState = transactionState;
    }

    public double getAmount() {
        return amount;
    }
}
