package com.company.main;

import java.time.LocalDate;

public class ChangeAPREvent extends TransactionEvent {

    public final double APR;

    ChangeAPREvent(double APR, LocalDate transactionDate) {
        this.APR = APR;
        this.transactionDate = transactionDate;
    }

    @Override
    protected void process(CreditCard card) {
        card.handleChangeAPR(this);
    }
}
