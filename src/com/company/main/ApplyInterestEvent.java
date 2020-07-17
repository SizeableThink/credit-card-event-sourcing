package com.company.main;

import java.time.LocalDate;

public class ApplyInterestEvent extends TransactionEvent {

    public final double amount;

    ApplyInterestEvent(double amount, LocalDate transactionDate) {
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    @Override
    protected void process(CreditCard card) {
        card.handleInterest(this);
    }
}
