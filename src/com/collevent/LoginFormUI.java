package com.collevent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFormUI extends JDialog implements ActionListener {
    private static final String TITLE = "Login";
    private static final int WIDTH = 350;
    private static final int HEIGHT = 200;
    private JTextField userNameTextField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // Must be JDialog, taking the parent Frame
    public LoginFormUI(Frame owner) {
        super(owner, TITLE, true);
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(getOwner());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(true);
        mainPanel.setBackground(new Color(248, 248, 248)); // Slightly off-white for contrast

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.WEST;

        mainPanel.add(new JLabel("Username:"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        userNameTextField = new JTextField(15);
        userNameTextField.setBackground(Color.WHITE);
        mainPanel.add(userNameTextField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Password:"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(15);
        passwordField.setBackground(Color.WHITE);
        mainPanel.add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        mainPanel.add(loginButton, constraints);

        add(mainPanel, BorderLayout.CENTER);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = userNameTextField.getText();
            char[] passwordChars = passwordField.getPassword();

            if (username.isEmpty() || passwordChars.length == 0) {
                JOptionPane.showMessageDialog(this, "Please enter a username and password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // NOTE: Assuming UserManager.authenticateUser is correctly defined and available
            User user = UserManager.authenticateUser(username, new String(passwordChars));

            if (user != null) {
                handleSuccessfulLogin(user);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSuccessfulLogin(User user) {
        if (user instanceof Student) {
            Student student = (Student) user;
            // FIX: Pass both the student object AND the studentId
            new StudentDashboardUI(student, student.getStudentId()).setVisible(true);
        } else if (user instanceof Administrator) {
            new AdminDashboardUI((Administrator) user).setVisible(true);
        }
    }
}
