package gui;

import services.DataService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboard extends JFrame {
    private DataService dataService;
    private LoginFrame loginFrame;

    public AdminDashboard(DataService dataService, LoginFrame loginFrame) {
        this.dataService = dataService;
        this.loginFrame = loginFrame;

        setTitle("Admin Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1));

        JLabel titleLabel = new JLabel("Create New User Account", SwingConstants.CENTER);
        add(titleLabel);

        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        formPanel.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Initial Balance:"));
        JTextField balanceField = new JTextField();
        formPanel.add(balanceField);
        add(formPanel);

        JButton createButton = new JButton("Create Account");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String balanceStr = balanceField.getText();

                try {
                    double balance = Double.parseDouble(balanceStr);
                    boolean success = dataService.createUser(username, password, "USER", balance);
                    if (success) {
                        JOptionPane.showMessageDialog(AdminDashboard.this, "User created successfully!");
                        usernameField.setText("");
                        passwordField.setText("");
                        balanceField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(AdminDashboard.this, "Username already exists!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Invalid balance format!");
                }
            }
        });
        add(createButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            setVisible(false);
            loginFrame.setVisible(true);
        });
        add(logoutButton);
    }
}
