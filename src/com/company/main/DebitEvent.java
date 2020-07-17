package com.company.main;

import java.time.LocalDate;

public class DebitEvent extends TransactionEvent {

    public final double amount;

    DebitEvent(double amount, LocalDate transactionDate) {
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    @Override
    protected void process(CreditCard card) {
        card.handleCharge(this);
    }
}
