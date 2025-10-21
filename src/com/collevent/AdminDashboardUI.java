package com.collevent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdminDashboardUI extends JFrame implements ActionListener {
    private Administrator admin;
    private JTable eventTable;
    private JButton addButton, updateButton, deleteButton, refreshButton, viewRegButton;
    private DefaultTableModel eventTableModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public AdminDashboardUI(Administrator admin) {
        this.admin = admin;
        setTitle("Admin Dashboard - " + admin.getAdminName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600); // Increased width to fit the new button
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Event Table
        eventTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Date", "Location"}, 0);
        eventTable = new JTable(eventTableModel);
        eventTable.setAutoCreateRowSorter(true);
        eventTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane eventScrollPane = new JScrollPane(eventTable);
        mainPanel.add(eventScrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Add Event");
        updateButton = new JButton("Update Selected");
        deleteButton = new JButton("Delete Selected");
        viewRegButton = new JButton("View Registrations");
        refreshButton = new JButton("Refresh");

        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        viewRegButton.addActionListener(this);
        refreshButton.addActionListener(this);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewRegButton);
        buttonPanel.add(refreshButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        populateEventTable();
    }

    private void populateEventTable() {
        eventTableModel.setRowCount(0);
        List<Event> events = EventManager.getAllEvents();
        for (Event event : events) {
            eventTableModel.addRow(new Object[]{
                    event.getEventId(),
                    event.getTitle(),
                    dateFormat.format(event.getEventDate()),
                    event.getLocation()
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            openEventForm(null);
        } else if (e.getSource() == updateButton) {
            handleUpdate();
        } else if (e.getSource() == deleteButton) {
            handleDelete();
        } else if (e.getSource() == viewRegButton) {
            handleViewRegistrations();
        } else if (e.getSource() == refreshButton) {
            populateEventTable();
        }
    }

    private void handleUpdate() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int modelRow = eventTable.convertRowIndexToModel(selectedRow);
        int eventId = (int) eventTableModel.getValueAt(modelRow, 0);
        Event event = EventManager.getEventById(eventId);
        if (event != null) {
            openEventForm(event);
        }
    }

    private void handleDelete() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this event?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = eventTable.convertRowIndexToModel(selectedRow);
            int eventId = (int) eventTableModel.getValueAt(modelRow, 0);
            EventManager.deleteEvent(eventId);
            populateEventTable();
        }
    }

    
    private void handleViewRegistrations() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to view registrations.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int modelRow = eventTable.convertRowIndexToModel(selectedRow);
        int eventId = (int) eventTableModel.getValueAt(modelRow, 0);
        String eventTitle = (String) eventTableModel.getValueAt(modelRow, 1);

        List<Student> students = RegistrationManager.getStudentsByEventId(eventId);

        JDialog registrationDialog = new JDialog(this, "Registrations for: " + eventTitle, true);
        registrationDialog.setSize(600, 450); // Increased size
        registrationDialog.setLocationRelativeTo(this);
        registrationDialog.setLayout(new BorderLayout());

        DefaultTableModel regTableModel = new DefaultTableModel(new Object[]{"Student ID", "Name", "Username", "Email"}, 0);

        if (students.isEmpty()) {
            JLabel emptyLabel = new JLabel("No students registered for this event yet.", SwingConstants.CENTER);
            registrationDialog.add(emptyLabel, BorderLayout.CENTER);
        } else {
            for (Student student : students) {
                regTableModel.addRow(new Object[]{
                        student.getStudentId(),
                        student.getStudentName(),
                        student.getUsername(),
                        student.getEmail()
                });
            }
            JTable regTable = new JTable(regTableModel);
            regTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            registrationDialog.add(new JScrollPane(regTable), BorderLayout.CENTER);


            JButton removeStudentButton = new JButton("Remove from Event");

            removeStudentButton.addActionListener(e -> {
                int studentSelectedRow = regTable.getSelectedRow();
                if (studentSelectedRow == -1) {
                    JOptionPane.showMessageDialog(registrationDialog, "Please select a student to remove from this event.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int studentModelRow = regTable.convertRowIndexToModel(studentSelectedRow);
                int studentIdToRemove = (int) regTableModel.getValueAt(studentModelRow, 0);
                String studentName = (String) regTableModel.getValueAt(studentModelRow, 1);

                int confirmRemove = JOptionPane.showConfirmDialog(
                        registrationDialog,
                        "Are you sure you want to remove " + studentName + " from this event?",
                        "Confirm Removal",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirmRemove == JOptionPane.YES_OPTION) {
                    RegistrationManager.unregisterEvent(studentIdToRemove, eventId);

                    JOptionPane.showMessageDialog(registrationDialog, studentName + " has been removed from " + eventTitle + ".", "Success", JOptionPane.INFORMATION_MESSAGE);

                    registrationDialog.dispose();
                    handleViewRegistrations();
                }
            });

            JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            southPanel.add(removeStudentButton);
            registrationDialog.add(southPanel, BorderLayout.SOUTH);
        }

        // 6. Add a label for total count
        JLabel countLabel = new JLabel("Total Registered: " + students.size() + " Students.", SwingConstants.CENTER);
        countLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        registrationDialog.add(countLabel, BorderLayout.NORTH);

        // 7. Display the dialog
        registrationDialog.setVisible(true);
    }


    private void openEventForm(Event eventToEdit) {
        JDialog dialog = new JDialog(this, eventToEdit == null ? "Add New Event" : "Update Event", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridBagLayout());
        dialog.setLocationRelativeTo(this);

        JTextField titleField = new JTextField(eventToEdit != null ? eventToEdit.getTitle() : "", 20);
        JTextArea descriptionArea = new JTextArea(eventToEdit != null ? eventToEdit.getDescription() : "", 4, 20);
        JTextField dateField = new JTextField(eventToEdit != null ? dateFormat.format(eventToEdit.getEventDate()) : "", 20);
        JTextField locationField = new JTextField(eventToEdit != null ? eventToEdit.getLocation() : "", 20);
        JButton saveButton = new JButton("Save");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; dialog.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; dialog.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.5; dialog.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weighty = 0; dialog.add(new JLabel("Date (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; dialog.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; dialog.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; dialog.add(locationField, gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST; dialog.add(saveButton, gbc);

        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText();
                String desc = descriptionArea.getText();
                Date date = dateFormat.parse(dateField.getText());
                String location = locationField.getText();

                if (title.isEmpty() || desc.isEmpty() || location.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (eventToEdit == null) {
                    // Create new event
                    Event newEvent = new Event(title, desc, date, location, admin.getAdminId());
                    EventManager.createEvent(newEvent);
                } else {
                    // Update existing event
                    eventToEdit.setTitle(title);
                    eventToEdit.setDescription(desc);
                    eventToEdit.setEventDate(date);
                    eventToEdit.setLocation(location);
                    EventManager.updateEvent(eventToEdit);
                }
                populateEventTable();
                dialog.dispose();

            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use yyyy-MM-dd.", "Date Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }
}
