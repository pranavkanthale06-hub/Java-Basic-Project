package services;

import models.Transaction;
import models.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DataService {
    private static final String USERS_FILE = "users.dat";
    private static final String TRANSACTIONS_FILE = "transactions.dat";

    private Map<String, User> users = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();

    public DataService() {
        loadData();
        if (users.isEmpty()) {
            // Seed default admin
            createUser("admin", "admin123", "ADMIN", 0);
            System.out.println("Default admin account created: admin / admin123");
        }
    }

    public User authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean createUser(String username, String password, String role, double initialBalance) {
        if (users.containsKey(username)) {
            return false;
        }
        User newUser = new User(username, password, role, initialBalance);
        users.put(username, newUser);
        saveUsers();
        return true;
    }

    public void addTransaction(String username, String type, double amount, String targetUser) {
        Transaction tx = new Transaction(UUID.randomUUID().toString(), username, type, amount, targetUser);
        transactions.add(tx);
        saveTransactions();

        // Update balances
        User user = users.get(username);
        if (user != null) {
            if ("DEPOSIT".equals(type)) {
                user.setBalance(user.getBalance() + amount);
            } else if ("WITHDRAW".equals(type)) {
                user.setBalance(user.getBalance() - amount);
            } else if ("TRANSFER".equals(type)) {
                user.setBalance(user.getBalance() - amount);
                User target = users.get(targetUser);
                if (target != null) {
                    target.setBalance(target.getBalance() + amount);
                    // Create a receiving transaction record for the target?
                    // For simplicity, we just update balance. The sender's record shows the
                    // transfer.
                }
            }
            saveUsers();
        }
    }

    public List<Transaction> getTransactionsForUser(String username) {
        return transactions.stream()
                .filter(t -> t.getUsername().equals(username)
                        || (t.getTargetUser() != null && t.getTargetUser().equals(username)))
                .collect(Collectors.toList());
    }

    public User getUser(String username) {
        return users.get(username);
    }

    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            users = (Map<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing users found or error loading users: " + e.getMessage());
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TRANSACTIONS_FILE))) {
            transactions = (List<Transaction>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing transactions found or error loading transactions: " + e.getMessage());
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTransactions() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TRANSACTIONS_FILE))) {
            oos.writeObject(transactions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
