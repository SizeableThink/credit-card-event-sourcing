package com.company.main;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        CreditCard creditCard = new CreditCard(35, 1000, LocalDate.of(2018, 3, 1));
        System.out.println(creditCard);
        creditCard.chargeCard(500, LocalDate.of(2018, 3, 11));
        creditCard.chargeCard(200, LocalDate.of(2018, 3, 15));
        System.out.println(creditCard);
        creditCard.chargeCard(50, LocalDate.of(2018, 4, 5));
        System.out.println(creditCard);
        System.out.println(creditCard.getBalanceAsOfDate(LocalDate.of(2018, 5, 1)));
    }
}
