package com.collevent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationFormUI extends JDialog implements ActionListener {
    private JTextField usernameField, emailField, nameField;
    private JPasswordField passwordField;
    private JRadioButton studentRadioButton, adminRadioButton;
    private JButton registerButton;

    public RegistrationFormUI(Frame owner) {
        super(owner, "Registration", true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        initializeUI();
    }

    private void initializeUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;



        // Username
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; usernameField = new JTextField(15); panel.add(usernameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; emailField = new JTextField(15); panel.add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; passwordField = new JPasswordField(15); panel.add(passwordField, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; nameField = new JTextField(15); panel.add(nameField, gbc);

        // User Type
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; panel.add(new JLabel("Account Type:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel userTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        studentRadioButton = new JRadioButton("Student");
        studentRadioButton.setSelected(true);
        adminRadioButton = new JRadioButton("Administrator");
        ButtonGroup userTypeGroup = new ButtonGroup();
        userTypeGroup.add(studentRadioButton);
        userTypeGroup.add(adminRadioButton);
        userTypePanel.add(studentRadioButton);
        userTypePanel.add(adminRadioButton);
        panel.add(userTypePanel, gbc);

        // Register Button
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        panel.add(registerButton, gbc);

        add(panel, BorderLayout.CENTER);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText();
            String userType = studentRadioButton.isSelected() ? "Student" : "Administrator";

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user;
            if (userType.equals("Student")) {
                user = new Student();
            } else {
                user = new Administrator();
            }
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setUserType(userType);

            int generatedId = UserManager.registerUser(user, name);

            if (generatedId != -1) {
                JOptionPane.showMessageDialog(this, userType + " registration successful! ID: " + generatedId, "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Username or email may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
