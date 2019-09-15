package com.revolut.transaction_api.models;

public enum TransactionState {
    QUEUED,
    RUNNING,
    DONE,
    ABORTED,
    FAILED
}