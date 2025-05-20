package com.alephys;

import java.time.LocalDate;

class Transaction {
    String type; 
    double amount;
    String category;
    LocalDate date;

    Transaction(String type, double amount, String category, LocalDate date) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    @Override
    public String toString() {
        return type + "," + amount + "," + category + "," + date;
    }
}