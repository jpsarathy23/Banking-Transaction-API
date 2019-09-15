package com.revolut.transaction_api.models;

import com.revolut.transaction_api.interfaces.Transactable;
import com.revolut.transaction_api.exceptions.InsufficientBalanceException;
import com.revolut.transaction_api.exceptions.InvalidKeyTransaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Account implements Transactable {
    private int accountId;
    private String name;
    private double balance;
    private int transactionId = -1;
    private double workingBalance;
    public Map<Integer, Long> transactionIdReadTimestampMap = new ConcurrentHashMap<>();
    public Map<Integer, Long> transactionIdWriteTimestampMap = new ConcurrentHashMap<>();

    public Account(int accountId, String name, double balance) {
        this.accountId = accountId;
        this.name = name;
        this.balance = balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public synchronized void debit(Transaction transaction) throws InvalidKeyTransaction, InsufficientBalanceException {
        if (transactionId != transaction.getTransactionId()) {
            throw new InvalidKeyTransaction();
        } else if (balance < transaction.getAmount()) {
            throw new InsufficientBalanceException();
        } else {
            workingBalance -= transaction.getAmount();
        }
    }

    public synchronized void credit(Transaction transaction) throws InvalidKeyTransaction {
        if (transactionId != transaction.getTransactionId()) {
            throw new InvalidKeyTransaction();
        } else {
            workingBalance += transaction.getAmount();
        }
    }

    @Override
    public synchronized void setTransaction(Transaction transaction) throws InvalidKeyTransaction {
        if (transactionId == -1) {
            transactionId = transaction.getTransactionId();
            workingBalance = balance;
        } else {
            //System.out.println("SET TXN FAILED. LOCKED BY " + transactionId + " CURR " + transaction.getTransactionId());
            throw new InvalidKeyTransaction();
        }
    }

    @Override
    public synchronized boolean canCommit(Transaction transaction) throws InvalidKeyTransaction {
        if (transactionId != transaction.getTransactionId()) {
            throw new InvalidKeyTransaction();
        } else {
            return true;
        }
    }

    @Override
    public synchronized void commit(Transaction transaction) throws InvalidKeyTransaction {
        if (transactionId != transaction.getTransactionId()) {
            throw new InvalidKeyTransaction();
        } else {
            balance = workingBalance;
            transactionId = -1;
        }
    }

    @Override
    public void abort(Transaction transaction) throws InvalidKeyTransaction {
        if (transactionId != transaction.getTransactionId()) {
            throw new InvalidKeyTransaction();
        } else {
            transactionId = -1;
            //System.out.println("TXN ABORTED " + transaction.getTransactionId());
        }
    }
}
