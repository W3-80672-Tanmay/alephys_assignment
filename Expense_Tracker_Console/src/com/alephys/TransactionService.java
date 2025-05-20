package com.alephys;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TransactionService {
	 static List<Transaction> transactions = new ArrayList<>();
	    static Scanner scanner = new Scanner(System.in);
	    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static String getTransactionTypeFromUser() {
        while (true) {
            System.out.print("Enter transaction type (1 - Income, 2 - Expense): ");
            String input = scanner.nextLine().trim();

            if (input.equals("1")) return "Income";
            if (input.equals("2")) return "Expense";

            System.out.println("Invalid input. Please enter 1 for Income or 2 for Expense.");
        }
    }

       static void addTransaction(String type) {
        double amount;
        while (true) {
            System.out.print("Enter amount: ");
            if (!scanner.hasNextDouble()) {
                System.out.println("Invalid amount. Please enter a number.");
                scanner.next(); // discard invalid input
                continue;
            }
            amount = scanner.nextDouble();
            scanner.nextLine(); // consume newline

            if (amount <= 0) {
                System.out.println("Amount must be greater than 0. Please try again.");
                continue;
            }
            break;
        }

        List<String> allowedCategories = type.equalsIgnoreCase("Income")
                ? Arrays.asList("Salary", "Business")
                : Arrays.asList("Food", "Rent", "Travel");


        List<String> allowedLower = new ArrayList<>();
        for (String cat : allowedCategories) {
            allowedLower.add(cat.toLowerCase());
        }

        String category;
        while (true) {
            System.out.print("Enter category " + allowedCategories + ": ");
            category = scanner.nextLine().trim().toLowerCase();

            if (allowedLower.contains(category)) {
                category = category.substring(0, 1).toUpperCase() + category.substring(1);
                break;
            }

            System.out.println("Invalid category. Please enter one from " + allowedCategories);
        }

        LocalDate date;
        while (true) {
            System.out.print("Enter date (yyyy-MM-dd): ");
            String dateInput = scanner.nextLine().trim();
            try {
                date = LocalDate.parse(dateInput, formatter);
                break;
            } catch (Exception e) {
                System.out.println("Invalid date format. Please enter in yyyy-MM-dd format.");
            }
        }

        transactions.add(new Transaction(type, amount, category, date));
        System.out.println(type + " added successfully.");
    }

    static void viewMonthlySummary() {
        System.out.print("Enter month and year (yyyy-MM): ");
        String input = scanner.nextLine();
        int year;
        int month;

        try {
            String[] parts = input.split("-");
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
        } catch (Exception e) {
            System.out.println("Invalid input format. Use yyyy-MM.");
            return;
        }

        double totalIncome = 0, totalExpense = 0;
        Map<String, Double> incomeCategories = new HashMap<>();
        Map<String, Double> expenseCategories = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.date.getYear() == year && t.date.getMonthValue() == month) {
                if (t.type.equals("Income")) {
                    totalIncome += t.amount;
                    incomeCategories.put(t.category,
                            incomeCategories.getOrDefault(t.category, 0.0) + t.amount);
                } else {
                    totalExpense += t.amount;
                    expenseCategories.put(t.category,
                            expenseCategories.getOrDefault(t.category, 0.0) + t.amount);
                }
            }
        }

        System.out.println("\n--- Monthly Summary for " + input + " ---");
        System.out.println("Total Income: " + totalIncome);
        for (Map.Entry<String, Double> entry : incomeCategories.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("Total Expense: " + totalExpense);
        for (Map.Entry<String, Double> entry : expenseCategories.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }

    // Load transactions from a file
    static void loadFromFile() {
        String fileName = getValidatedTxtFileName("load from");

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int count = 0;
            List<Transaction> loaded = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 4) continue;
                String type = parts[0];
                double amount = Double.parseDouble(parts[1]);
                String category = parts[2];
                LocalDate date = LocalDate.parse(parts[3], formatter);
                loaded.add(new Transaction(type, amount, category, date));
                count++;
            }

            transactions.addAll(loaded);
            System.out.println("Loaded " + count + " transactions from " + fileName);

            if (!loaded.isEmpty()) {
                System.out.print("Do you want to see a (1) summary or (2) full preview? Enter 1, 2, or any other key to skip: ");
                String choice = scanner.nextLine().trim();

                if (choice.equals("1")) {
                    showSummaryOfLoadedData(loaded);
                } else if (choice.equals("2")) {
                    for (Transaction t : loaded) {
                        System.out.println(t);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }



    
    static void saveToFile() {
        String fileName = getValidatedTxtFileName("save to");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Transaction t : transactions) {
                bw.write(t.toString());
                bw.newLine();
            }
            System.out.println("Transactions saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    static void showSummaryOfLoadedData(List<Transaction> list) {
        double income = 0, expense = 0;
        for (Transaction t : list) {
            if (t.type.equalsIgnoreCase("Income")) income += t.amount;
            else if (t.type.equalsIgnoreCase("Expense")) expense += t.amount;
        }
        System.out.println(" Summary of Loaded Data:");
        System.out.println("Total Income: ₹" + income);
        System.out.println("Total Expense: ₹" + expense);
    }

    
    static String getValidatedTxtFileName(String action) {
        while (true) {
            System.out.print("Enter file name to " + action + " (must be .txt): ");
            String input = scanner.nextLine().trim();

            // If no extension is given, append .txt
            if (!input.contains(".")) {
                input += ".txt";
                return input;
            }

            // If ends with .txt, accept
            if (input.toLowerCase().endsWith(".txt")) {
                return input;
            }

            // Invalid extension
            System.out.println("Invalid file extension. Only .txt files are allowed.");
        }
    }

}
