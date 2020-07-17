package com.company.main;

import java.time.LocalDate;

public class CreditEvent extends TransactionEvent {

    public final double amount;

    public CreditEvent(double amount, LocalDate transactionDate) {
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    @Override
    protected void process(CreditCard card) {
        card.handlePayment(this);
    }
}
