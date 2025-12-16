package gui;

import models.User;
import services.DataService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private DataService dataService;

    public LoginFrame(DataService dataService) {
        this.dataService = dataService;

        setTitle("Transaction System Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Username:"));
        JTextField usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                User user = dataService.authenticate(username, password);
                if (user != null) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Login Successful!");
                    setVisible(false);
                    if ("ADMIN".equals(user.getRole())) {
                        new AdminDashboard(dataService, LoginFrame.this).setVisible(true);
                    } else {
                        new UserDashboard(dataService, user, LoginFrame.this).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid credentials!");
                }
            }
        });
        add(loginButton);
    }
}
