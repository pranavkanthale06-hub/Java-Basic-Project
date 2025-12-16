package gui;

import models.Transaction;
import models.User;
import services.DataService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserDashboard extends JFrame {
    private DataService dataService;
    private User currentUser;
    private LoginFrame loginFrame;
    private JLabel balanceLabel;
    private JTable transactionTable;
    private DefaultTableModel tableModel;

    public UserDashboard(DataService dataService, User user, LoginFrame loginFrame) {
        this.dataService = dataService;
        this.currentUser = user;
        this.loginFrame = loginFrame;

        setTitle("User Dashboard - " + user.getUsername());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel: Balance and Logout
        JPanel topPanel = new JPanel(new BorderLayout());
        balanceLabel = new JLabel("Balance: $" + user.getBalance());
        balanceLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(balanceLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            setVisible(false);
            loginFrame.setVisible(true);
        });
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Actions and History
        JTabbedPane tabbedPane = new JTabbedPane();

        // Actions Tab
        JPanel actionsPanel = new JPanel(new GridLayout(3, 1));

        // Deposit
        JPanel depositPanel = new JPanel();
        depositPanel.add(new JLabel("Amount:"));
        JTextField depositField = new JTextField(10);
        depositPanel.add(depositField);
        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(e -> handleTransaction("DEPOSIT", depositField.getText(), null));
        depositPanel.add(depositButton);
        actionsPanel.add(depositPanel);

        // Withdraw
        JPanel withdrawPanel = new JPanel();
        withdrawPanel.add(new JLabel("Amount:"));
        JTextField withdrawField = new JTextField(10);
        withdrawPanel.add(withdrawField);
        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(e -> handleTransaction("WITHDRAW", withdrawField.getText(), null));
        withdrawPanel.add(withdrawButton);
        actionsPanel.add(withdrawPanel);

        // Transfer
        JPanel transferPanel = new JPanel();
        transferPanel.add(new JLabel("To User:"));
        JTextField targetUserField = new JTextField(10);
        transferPanel.add(targetUserField);
        transferPanel.add(new JLabel("Amount:"));
        JTextField transferAmountField = new JTextField(10);
        transferPanel.add(transferAmountField);
        JButton transferButton = new JButton("Transfer");
        transferButton.addActionListener(
                e -> handleTransaction("TRANSFER", transferAmountField.getText(), targetUserField.getText()));
        transferPanel.add(transferButton);
        actionsPanel.add(transferPanel);

        tabbedPane.addTab("Actions", actionsPanel);

        // History Tab
        String[] columnNames = { "Date", "Type", "Amount", "Details" };
        tableModel = new DefaultTableModel(columnNames, 0);
        transactionTable = new JTable(tableModel);
        updateTransactionHistory();
        tabbedPane.addTab("History", new JScrollPane(transactionTable));

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void handleTransaction(String type, String amountStr, String targetUser) {
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive.");
                return;
            }

            if ("WITHDRAW".equals(type) && currentUser.getBalance() < amount) {
                JOptionPane.showMessageDialog(this, "Insufficient funds.");
                return;
            }

            if ("TRANSFER".equals(type)) {
                if (currentUser.getBalance() < amount) {
                    JOptionPane.showMessageDialog(this, "Insufficient funds.");
                    return;
                }
                if (dataService.getUser(targetUser) == null) {
                    JOptionPane.showMessageDialog(this, "Target user not found.");
                    return;
                }
            }

            dataService.addTransaction(currentUser.getUsername(), type, amount, targetUser);
            // Refresh user data
            currentUser = dataService.getUser(currentUser.getUsername());
            balanceLabel.setText("Balance: $" + currentUser.getBalance());
            updateTransactionHistory();
            JOptionPane.showMessageDialog(this, "Transaction successful!");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
        }
    }

    private void updateTransactionHistory() {
        tableModel.setRowCount(0);
        List<Transaction> transactions = dataService.getTransactionsForUser(currentUser.getUsername());
        for (Transaction t : transactions) {
            String details = t.getTargetUser() != null ? "To: " + t.getTargetUser() : "";
            if (t.getTargetUser() != null && t.getTargetUser().equals(currentUser.getUsername())) {
                // If I am the receiver
                details = "From: " + t.getUsername();
            }
            Object[] row = { t.getTimestamp(), t.getType(), t.getAmount(), details };
            tableModel.addRow(row);
        }
    }
}
