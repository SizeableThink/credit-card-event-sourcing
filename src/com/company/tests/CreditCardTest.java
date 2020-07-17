package com.company.tests;

import com.company.main.CreditCard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sun.jvm.hotspot.utilities.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CreditCardTest {

    /*
    * Test Scenario 1
            A customer opens a credit card with a $1,000.00 limit at a 35% APR.
            The customer charges $500 on opening day (outstanding balance becomes $500).
            The customer does not make any more charges or any payments for 30 days.
            The total outstanding balance owed 30 days after opening should be $514.38.
            500 * (0.35 / 365) * 30 = 14.38
    */

    @Test
    void testScenario1() {
        LocalDate openDate = LocalDate.of(2018,3,1);
        CreditCard creditCard = new CreditCard(35, 1000, openDate);
        creditCard.chargeCard(500, LocalDate.of(2018, 3, 1));
        //The customer charges $500 on opening day (outstanding balance becomes $500).
        assertEquals(500, creditCard.getBalanceAsOfDate(openDate));
        double balance = creditCard.getBalanceAsOfDate(openDate.plusDays(30));
        BigDecimal finalBalance = new BigDecimal(balance);
        finalBalance = finalBalance.setScale(2, RoundingMode.HALF_UP);
        //The total outstanding balance owed 30 days after opening should be $514.38.
        assertEquals(BigDecimal.valueOf(514.38), finalBalance);
    }

    /*
    * Test Scenario 2
        A customer opens a credit card with a $1,000.00 limit at a 35% APR.
        The customer charges $500 on opening day (outstanding balance becomes $500).
        15 days after opening, the customer pays $200 (outstanding balance becomes $300).
        25 days after opening, the customer charges another $100 (outstanding balance becomes $400).
        The total outstanding balance owed on day 30 should be $411.99.
        (500 * 0.35 / 365 * 15) + (300 * 0.35 / 365 * 10) + (400 * 0.35 / 365 * 5) = 11.99
    */

    @Test
    void testScenario2() {
        LocalDate openDate = LocalDate.of(2018,3,1);
        CreditCard creditCard = new CreditCard(35, 1000, openDate);
        creditCard.chargeCard(500, openDate);
        //The customer charges $500 on opening day (outstanding balance becomes $500).
        assertEquals(500, creditCard.getBalanceAsOfDate(openDate));
        creditCard.makePayment(200, openDate.plusDays(15));
        //15 days after opening, the customer pays $200 (outstanding balance becomes $300).
        assertEquals(300, creditCard.getBalanceAsOfDate(openDate.plusDays(15)));
        creditCard.chargeCard(100, openDate.plusDays(25));
        //25 days after opening, the customer charges another $100 (outstanding balance becomes $400)
        assertEquals(400, creditCard.getBalanceAsOfDate(openDate.plusDays(25)));
        double balance = creditCard.getBalanceAsOfDate(openDate.plusDays(30));
        BigDecimal finalBalance = new BigDecimal(balance);
        finalBalance = finalBalance.setScale(2, RoundingMode.HALF_UP);
        //The total outstanding balance owed on day 30 should be $411.99.
        assertEquals(BigDecimal.valueOf(411.99), finalBalance);
    }

    @Test
    void testGetters() {
        LocalDate openDate = LocalDate.of(2018,5,1);
        CreditCard creditCard = new CreditCard(25, 2000, openDate);
        assertEquals(0, creditCard.getBalance());
        assertEquals(openDate, creditCard.getOpenDate());
        assertEquals(openDate.plusDays(30), creditCard.getApplyInterestDate());
        assertEquals(25, creditCard.getAPR());
        assertEquals(2000, creditCard.getCreditLimit());
        assertEquals(0,creditCard.getCurrentBalance());
    }

    @Test
    void testChangeAPREvent() {
        LocalDate openDate = LocalDate.of(2018,5,1);
        CreditCard creditCard = new CreditCard(35, 1000, openDate);
        creditCard.chargeCard(100, openDate.plusDays(5));
        //assert balance is 100 on day 5
        assertEquals(100, creditCard.getBalanceAsOfDate(openDate.plusDays(5)));
        //assert interest on 100 between day 5 and 10 (5 days) using APR 35 is .48
        assertEquals(0.48, creditCard.interestBetweenDates(openDate.plusDays(5), openDate.plusDays(10)), .005);
        creditCard.changeAPR(25, openDate.plusDays(10));
        //after APR changed assert interest on 100 between day 10 and 15 (5 days) using APR 25 is .34
        assertEquals(0.34, creditCard.interestBetweenDates(openDate.plusDays(10), openDate.plusDays(15)), .005);
    }

    @Test
    void testChangeCreditLimitEvent() {
        LocalDate openDate = LocalDate.of(2018,5,1);
        CreditCard creditCard = new CreditCard(35, 1000, openDate);
        assertEquals(1000, creditCard.getCreditLimit());
        creditCard.changeCreditLimit(2500, LocalDate.now());
        assertEquals(2500, creditCard.getCreditLimit());

    }

}