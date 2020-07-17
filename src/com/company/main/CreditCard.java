package com.company.main;

import java.time.LocalDate;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

public class CreditCard {

    private final UUID accountId;
    private double APR;
    private double creditLimit;
    private double balance;
    private double currentInterestAccumulated; //keeps running total of interest before it's applied to balance
    private final LocalDate openDate;
    private LocalDate applyInterestDate; //date that the interest should be applied
    private final TransactionProcessor processor;
    private final int interestPeriod; //specify number of days until interest is applied, currently set to 30

    //create card with specific open date
    public CreditCard(double APR, double creditLimit, LocalDate openDate) {
        this.interestPeriod = 30;
        this.accountId = UUID.randomUUID();
        this.APR = APR;
        this.creditLimit = creditLimit;
        this.balance = 0;
        this.currentInterestAccumulated = 0;
        this.openDate = openDate;
        this.applyInterestDate = openDate.plusDays(interestPeriod);
        this.processor = new TransactionProcessor();
    }

    //create card with today's date as open date (alternate constructor for ease of creating new cards)
    public CreditCard(double APR, double creditLimit) {
        this(APR, creditLimit, LocalDate.now());
    }

    //getters
    public UUID getAccountId() {
        return accountId;
    }

    public double getAPR() {
        return APR;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public double getBalance() {
        return balance;
    }

    public double getCurrentInterestAccumulated() {
        return currentInterestAccumulated;
    }

    public double getCurrentBalance() {
        return getBalanceAsOfDate(LocalDate.now());
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public LocalDate getApplyInterestDate() {
        return applyInterestDate;
    }

    /*
    * check to see that an event has been processed
    * while the date is on or after the interest applied date
    * get the date of the last event
    * get interest total between last transaction and the apply interest date
    * apply the interest - adds interest to balance, resets running interest total to zero, moves apply interest date
    * once while loop is finished add interest accrued to running total between last transaction date and date given
    */
    private void reconcileAnyInterest (LocalDate date) {
        if (processor.events.size() > 0) {
            while (date.isAfter(applyInterestDate) || date.isEqual(applyInterestDate)) {
                LocalDate lastTransactionDate = processor.lastTransactionEvent();
                double interestToAccrue = interestBetweenDates(lastTransactionDate, applyInterestDate);
                accrueInterest(interestToAccrue);
                applyInterest(currentInterestAccumulated, applyInterestDate);
            }
            LocalDate lastTransactionDate = processor.lastTransactionEvent();
            accrueInterest(interestBetweenDates(lastTransactionDate, date));
        }
    }

    /*
    * Using temporary "hypothetical" card, process events up to a date in the past or future to
    * get accurate data as of that date*/
    public double getBalanceAsOfDate(LocalDate date) {
        CreditCard tempCard = new CreditCard(APR, creditLimit, openDate);
        int i = 0;
        while (i < processor.events.size() && eventDateIsOnOrBefore(processor.events.get(i), date)) {
            TransactionEvent tempEvent = processor.events.get(i);
            tempCard.processor.process(tempEvent, tempCard);
            i++;
        }
        tempCard.reconcileAnyInterest(date);
        return tempCard.getBalance();
    }

    //add interest to running interest total
    private void accrueInterest(double interest) {
        currentInterestAccumulated += interest;
    }

    //return the interest accumulated between 2 dates
    public double interestBetweenDates(LocalDate date1, LocalDate date2) {
        long days = DAYS.between(date1, date2);
        return balance * ((APR/100)/365) * days;
    }

    private Boolean eventDateIsOnOrBefore (TransactionEvent e, LocalDate date) {
        return e.getTransactionDate().isBefore(date) || e.getTransactionDate().isEqual(date);
    }

    //instantiate and dispatch a debit event
    public void chargeCard(double amount, LocalDate date) {
        DebitEvent debitEvent = new DebitEvent(amount, date);
        processor.process(debitEvent, this);
    }

    //instantiate and dispatch a credit event
    public void makePayment(double amount, LocalDate date) {
        CreditEvent creditEvent = new CreditEvent(amount, date);
        processor.process(creditEvent, this);
    }

    //instantiate and dispatch an apply interest event
    private void applyInterest(double amount, LocalDate date) {
        ApplyInterestEvent applyInterestEvent = new ApplyInterestEvent(amount, date);
        processor.process(applyInterestEvent, this);
    }

    //instantiate and dispatch a change APR event
    public void changeAPR(double APR, LocalDate date) {
        ChangeAPREvent changeAPREvent = new ChangeAPREvent(APR, date);
        processor.process(changeAPREvent, this);
    }

    //instantiate and dispatch a change credit limit event
    public void changeCreditLimit(double creditLimit, LocalDate date) {
        ChangeCreditLimitEvent changeCreditLimitEvent = new ChangeCreditLimitEvent(creditLimit, date);
        processor.process(changeCreditLimitEvent, this);
    }

    //when debit event is processed, check that limit isn't reached, reconcile interest then update balance
    void handleCharge(DebitEvent event) {
        if (balance + event.amount > creditLimit) {
            System.out.println("Charge denied, over credit limit");
        }
        else {
            reconcileAnyInterest(event.getTransactionDate());
            balance += event.amount;
        }
    }

    //when credit event is processed, reconcile interest and update balance
    void handlePayment(CreditEvent event) {
            reconcileAnyInterest(event.getTransactionDate());
            balance -= event.amount;
    }

    /*when apply interest event is processed, update balance, reset accumulated interest to 0, move interest applied
    date to the next interest period
     */
    void handleInterest(ApplyInterestEvent event) {
        balance += event.amount;
        currentInterestAccumulated = 0;
        applyInterestDate = applyInterestDate.plus(interestPeriod, DAYS);
    }

    //when change apr event is processed, update apr
    void handleChangeAPR(ChangeAPREvent event) {
        APR = event.APR;
    }

    //when change credit limit event is processed, update credit limit
    void handleChangeCreditLimit(ChangeCreditLimitEvent event) {
        creditLimit = event.creditLimit;
    }

    @Override
    public String toString() {
        return "Account UUID: " + accountId + "\n" +
                "Open date of account: " + openDate  + "\n" +
                "APR: " + APR + "\n" +
                "Credit Limit: " + creditLimit + "\n" +
                "Balance: " + balance + "\n" +
                "Interest Accrued: " + currentInterestAccumulated + "\n" +
                "Interest will be applied every " + interestPeriod + "days." + "\n" +
                "Next date interest will be applied: " + applyInterestDate + "\n";


    }
}
