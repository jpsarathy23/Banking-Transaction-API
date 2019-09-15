package com.revolut.transaction_api.helpers;


import com.revolut.transaction_api.models.Account;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AccountManager {
    private static AtomicInteger accountId = new AtomicInteger(0);
    private static ConcurrentHashMap<Integer, Account> accountMap = new ConcurrentHashMap<>();

    public static Account createAccount(String name, double balance) {
        int id = accountId.incrementAndGet();
        Account account = new Account(id, name, balance);
        accountMap.put(id, account);
        //System.out.println("Account created");
        return account;
    }

    public static Account getAccount(int id) {
        return accountMap.get(id);
    }

    public static int getTotalAccounts() {
        return accountMap.size();
    }

    public static int getLastAccountId() {
        return accountId.get();
    }
}
