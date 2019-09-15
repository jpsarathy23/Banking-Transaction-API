package com.revolut.transaction_api.interfaces;

import com.revolut.transaction_api.exceptions.InvalidKeyTransaction;
import com.revolut.transaction_api.models.Transaction;

public interface Transactable {
    void setTransaction(Transaction transaction) throws InvalidKeyTransaction;
    boolean canCommit(Transaction transaction) throws InvalidKeyTransaction;
    void commit(Transaction transaction) throws InvalidKeyTransaction;
    void abort(Transaction transaction) throws InvalidKeyTransaction;
}
