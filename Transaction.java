package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private String transactionId;
    private String username;
    private String type; // "DEPOSIT", "WITHDRAW", "TRANSFER"
    private double amount;
    private LocalDateTime timestamp;
    private String targetUser; // For transfers

    public Transaction(String transactionId, String username, String type, double amount, String targetUser) {
        this.transactionId = transactionId;
        this.username = username;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.targetUser = targetUser;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTargetUser() {
        return targetUser;
    }

    @Override
    public String toString() {
        return timestamp + " | " + type + " | " + amount + (targetUser != null ? " -> " + targetUser : "");
    }
}
