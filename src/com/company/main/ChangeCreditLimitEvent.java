package com.company.main;

import java.time.LocalDate;

public class ChangeCreditLimitEvent extends TransactionEvent {

    public final double creditLimit;

    ChangeCreditLimitEvent(double creditLimit, LocalDate transactionDate) {
        this.creditLimit = creditLimit;
        this.transactionDate = transactionDate;
    }

    @Override
    protected void process(CreditCard card) {
        card.handleChangeCreditLimit(this);
    }
}
