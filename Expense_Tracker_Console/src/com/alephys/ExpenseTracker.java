package com.alephys;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExpenseTracker {

//    static List<Transaction> transactions = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
//    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Expense Tracker Menu ---");
            System.out.println("1. Add Transaction (Income/Expense)");
            System.out.println("2. View Monthly Summary");
            System.out.println("3. Load Transactions from File");
            System.out.println("4. Save Transactions to File");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    String type = TransactionService.getTransactionTypeFromUser();
                    TransactionService.addTransaction(type);
                    break;
                case "2":
                	TransactionService.viewMonthlySummary();
                    break;
                case "3":
                	TransactionService.loadFromFile();
                    break;
                case "4":
                	TransactionService.saveToFile();
                    break;
                case "5":
                    System.out.println("Exiting. Goodbye Visit Again!");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }



}
