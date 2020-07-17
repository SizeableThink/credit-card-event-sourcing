package com.company.main;

import java.time.LocalDate;
import java.util.ArrayList;

class TransactionProcessor {

    final ArrayList<TransactionEvent> events = new ArrayList<>();

    public void process(TransactionEvent e, CreditCard card) {
        e.process(card);
        events.add(e);
    }

    public LocalDate lastTransactionEvent() {
        TransactionEvent mostRecent = events.get(events.size()-1);
        return mostRecent.getTransactionDate();
        }
    }
