package com.company.main;

import java.time.LocalDate;

abstract class TransactionEvent {

    LocalDate transactionDate;

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    abstract protected void process(CreditCard card);
}