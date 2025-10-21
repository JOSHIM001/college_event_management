package com.collevent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomePageUI extends JFrame implements ActionListener {
    private static final String TITLE = "College Event Registration System";
    private static final int WIDTH = 500;
    private static final int HEIGHT = 300;
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color BUTTON_COLOR = new Color(51, 153, 255);

    private JButton loginButton;
    private JButton registerButton;

    public WelcomePageUI() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeUI();
    }

    private void initializeUI() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Welcome Message
        JLabel welcomeLabel = new JLabel("Welcome to the College Event Registration!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(welcomeLabel, gbc);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setFont(BUTTON_FONT);
        loginButton.setBackground(BUTTON_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(this);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(loginButton, gbc);

        // Register Button
        registerButton = new JButton("Register");
        registerButton.setFont(BUTTON_FONT);
        registerButton.setBackground(BUTTON_COLOR);
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(this);
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(registerButton, gbc);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            openLoginForm();
        } else if (e.getSource() == registerButton) {
            openRegistrationForm();
        }
    }

    private void openLoginForm() {

        LoginFormUI loginForm = new LoginFormUI(this);
        loginForm.setVisible(true);
    }

    private void openRegistrationForm() {
        RegistrationFormUI registrationForm = new RegistrationFormUI(this);
        registrationForm.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WelcomePageUI mainFrame = new WelcomePageUI();
            mainFrame.setVisible(true);
        });
    }
}
