package com.collevent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class StudentDashboardUI extends JFrame implements ActionListener {
    private Student student;
    private int studentId;
    private JTabbedPane tabbedPane;
    private JTable availableEventTable, myRegistrationTable;
    private JButton registerButton, unregisterButton, refreshAvailableButton, refreshRegisteredButton;
    private DefaultTableModel availableEventTableModel, myRegistrationTableModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public StudentDashboardUI(Student student, int studentId) {
        this.student = student;
        this.studentId = studentId;
        setTitle("Student Dashboard - " + student.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        JPanel availablePanel = createAvailableEventsPanel();
        tabbedPane.addTab("Available Events", availablePanel);

        JPanel registeredPanel = createMyRegistrationsPanel();
        tabbedPane.addTab("My Registrations", registeredPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Initial data load
        populateAvailableEventTable();
        populateMyRegistrationTable();
    }

    // --- Panel Creation Methods ---

    private JPanel createAvailableEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Event Table Setup
        availableEventTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Date", "Location"}, 0);
        availableEventTable = new JTable(availableEventTableModel);
        availableEventTable.setAutoCreateRowSorter(true);
        availableEventTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(availableEventTable), BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        registerButton = new JButton("Register for Selected Event");
        refreshAvailableButton = new JButton("Refresh Available Events"); // Refresh for Available
        registerButton.addActionListener(this);
        refreshAvailableButton.addActionListener(this);

        buttonPanel.add(refreshAvailableButton);
        buttonPanel.add(registerButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMyRegistrationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Registration Table Setup
        myRegistrationTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Date", "Location", "Registration Date"}, 0);
        myRegistrationTable = new JTable(myRegistrationTableModel);
        myRegistrationTable.setAutoCreateRowSorter(true);
        myRegistrationTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(myRegistrationTable), BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        unregisterButton = new JButton("Unregister Selected Event");
        refreshRegisteredButton = new JButton("Refresh My Registrations"); // Refresh for Registered
        unregisterButton.addActionListener(this);
        refreshRegisteredButton.addActionListener(this); // ADD LISTENER

        buttonPanel.add(refreshRegisteredButton); // ADD REFRESH BUTTON
        buttonPanel.add(unregisterButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    // --- Data Handling Methods ---

    private void populateAvailableEventTable() {
        availableEventTableModel.setRowCount(0);
        List<Event> events = EventManager.getAllEvents();
        List<Registration> myRegistrations = RegistrationManager.getRegistrationsByStudentId(studentId);

        // Get IDs of registered events to filter
        List<Integer> registeredEventIds = myRegistrations.stream()
                .map(Registration::getEventId)
                .toList();

        for (Event event : events) {
            // Only show events the student has NOT registered for
            if (!registeredEventIds.contains(event.getEventId())) {
                availableEventTableModel.addRow(new Object[]{
                        event.getEventId(),
                        event.getTitle(),
                        dateFormat.format(event.getEventDate()),
                        event.getLocation()
                });
            }
        }
    }

    private void populateMyRegistrationTable() {
        myRegistrationTableModel.setRowCount(0);
        List<Registration> registrations = RegistrationManager.getRegistrationsByStudentId(studentId);

        for (Registration reg : registrations) {
            Event event = EventManager.getEventById(reg.getEventId());
            if (event != null) {
                myRegistrationTableModel.addRow(new Object[]{
                        event.getEventId(),
                        event.getTitle(),
                        dateFormat.format(event.getEventDate()),
                        event.getLocation(),
                        dateFormat.format(reg.getRegistrationDate()) // Show date of registration
                });
            }
        }

        // Update the window title with registration count
        int count = registrations.size();
        setTitle("Student Dashboard - " + student.getUsername() + " (Registered: " + count + "/3)");
    }

    private void handleRegistration() {
        int selectedRow = availableEventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to register for.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int modelRow = availableEventTable.convertRowIndexToModel(selectedRow);
        int eventId = (int) availableEventTable.getValueAt(modelRow, 0);
        String eventTitle = (String) availableEventTable.getValueAt(modelRow, 1);

        // Attempt registration (logic handles the 3-event limit)
        boolean success = RegistrationManager.registerForEvent(studentId, eventId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Successfully registered for: " + eventTitle, "Success", JOptionPane.INFORMATION_MESSAGE);
            // Refresh both tables after a successful action
            populateAvailableEventTable();
            populateMyRegistrationTable();
        } else {
            // RegistrationManager returns false if the 3-event limit is reached
            JOptionPane.showMessageDialog(this,
                    "Registration failed. You are currently registered for the maximum limit of 3 events.",
                    "Limit Reached", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleUnregistration() {
        int selectedRow = myRegistrationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to unregister from.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to unregister from this event?", "Confirm Unregistration", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = myRegistrationTable.convertRowIndexToModel(selectedRow);
            int eventId = (int) myRegistrationTable.getValueAt(modelRow, 0);
            String eventTitle = (String) myRegistrationTable.getValueAt(modelRow, 1);

            RegistrationManager.unregisterEvent(studentId, eventId);

            JOptionPane.showMessageDialog(this, "Successfully unregistered from: " + eventTitle, "Success", JOptionPane.INFORMATION_MESSAGE);
            // Refresh both tables after a successful action
            populateAvailableEventTable();
            populateMyRegistrationTable();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            handleRegistration();
        } else if (e.getSource() == unregisterButton) {
            handleUnregistration();
        } else if (e.getSource() == refreshAvailableButton) {
            populateAvailableEventTable();
            JOptionPane.showMessageDialog(this, "Available Events list refreshed.", "Refreshed", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == refreshRegisteredButton) {
            populateMyRegistrationTable();
            JOptionPane.showMessageDialog(this, "My Registrations list refreshed.", "Refreshed", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
