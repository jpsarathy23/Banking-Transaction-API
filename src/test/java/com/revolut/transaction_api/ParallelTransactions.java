package com.revolut.transaction_api;

import com.revolut.transaction_api.helpers.AccountManager;
import com.revolut.transaction_api.helpers.TransactionManager;
import com.revolut.transaction_api.interfaces.TransactionStrategy;
import com.revolut.transaction_api.models.Account;
import com.revolut.transaction_api.models.Transaction;
import com.revolut.transaction_api.models.TransactionState;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ParallelTransactions {
    private static int maxAccount = 50;
    private static List<Account> accountList;
    private static List<Transaction> transactionList;
    private static int maxTransferAmount = 100;
    private static int maxTransfers = 5000;
    private static int initialBalance = 1000;
    private static final int threadCount = 10;

    @BeforeClass
    public static void prepareAccounts() {
        accountList = new ArrayList<>();
        transactionList = new ArrayList<>();
        int counter = 0;
        while(counter < maxAccount) {
            accountList.add(AccountManager.createAccount("Account" + counter, initialBalance));
            counter++;
        }
        counter = 0;
        while(counter < maxTransfers) {
            String toAccount = Integer.toString((int)Math.ceil(Math.random() * maxAccount));
            String fromAccount = Integer.toString((int)Math.ceil(Math.random() * maxAccount));
            if (toAccount.equals("0") || fromAccount.equals("0") || fromAccount.equals(toAccount)) {
                continue;
            }
            counter++;
            double amount = (int) (Math.random() * maxTransferAmount);
            Transaction transaction = TransactionManager.createTransaction(fromAccount, toAccount, amount);
            transactionList.add(transaction);
        }
    }

    @Before
    public void resetAccountAndTransaction() {
        accountList.forEach(account -> account.setBalance(initialBalance));
        transactionList.forEach(transaction -> transaction.setTransactionState(TransactionState.QUEUED));
    }

    @Test
    public void testSynchronizedStrategy() {
        System.out.println("SYNCHRONIZED");
        new TransactionEngine(threadCount, TransactionStrategy.synchronisedTransactionStrategy()).executeTransactions(true);
        testCorrectness();
    }
    @Test
    public void testOptimisticTransactionStrategy() {
        System.out.println("OPTIMISTIC");
        new TransactionEngine(threadCount, TransactionStrategy.optimisticTransactionStrategy()).executeTransactions(true);
        testCorrectness();
    }
    @Test
    public void testOptimisticConcurrencyControlStrategy() {
        System.out.println("OCC");
        new TransactionEngine(threadCount, TransactionStrategy.optimisticTransactionStrategy()).executeTransactions(true);
        testCorrectness();
    }
    public void testCorrectness() {
        Map<TransactionState, Long> transactionStateMap = transactionList.stream().collect(
                Collectors.groupingBy(Transaction::getTransactionState, Collectors.counting())
        );
        while ((transactionStateMap.getOrDefault(TransactionState.QUEUED, 0L) +
                transactionStateMap.getOrDefault(TransactionState.RUNNING, 0L) +
                transactionStateMap.getOrDefault(TransactionState.ABORTED, 0L)) > 0L
        ) {
            transactionStateMap = transactionList.stream().collect(
                    Collectors.groupingBy(Transaction::getTransactionState, Collectors.counting())
            );
        }
        System.out.println(transactionStateMap);
        //accountList.forEach(account -> System.out.println(account.getName() + " " + account.getBalance()));
        assertEquals(maxAccount*initialBalance, getTotalBalance(), 0.1);

    }

    public double getTotalBalance() {
        return accountList.stream().map(Account::getBalance).mapToDouble(Double::doubleValue).sum();
    }
}
