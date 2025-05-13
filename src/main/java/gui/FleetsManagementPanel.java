package gui;

import models.TaxiFleet;
import models.TaxiFleetManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FleetsManagementPanel extends JPanel {
    private TaxiFleetManager fleetManager;
    private JTabbedPane parentTabbedPane;
    private JList<String> fleetsList;

    public FleetsManagementPanel(TaxiFleetManager fleetManager, JTabbedPane parentTabbedPane) {
        this.fleetManager = fleetManager;
        this.parentTabbedPane = parentTabbedPane;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Панель з кнопками
        JPanel buttonPanel = new JPanel();
        JButton createBtn = new JButton("Створити таксопарк");
        JButton openBtn = new JButton("Керувати таксопарком");

        createBtn.addActionListener(this::createFleet);
        openBtn.addActionListener(this::openFleet);

        buttonPanel.add(createBtn);
        buttonPanel.add(openBtn);
        add(buttonPanel, BorderLayout.NORTH);

        // Список таксопарків
        fleetsList = new JList<>();
        updateFleetsList();
        add(new JScrollPane(fleetsList), BorderLayout.CENTER);
    }

    private void createFleet(ActionEvent e) {
        String name = JOptionPane.showInputDialog(this, "Введіть назву таксопарку:");
        if (name != null && !name.trim().isEmpty()) {
            fleetManager.createFleet(name);
            updateFleetsList();
        }
    }

    private void openFleet(ActionEvent e) {
        int selectedIndex = fleetsList.getSelectedIndex();
        if (selectedIndex >= 0) {
            TaxiFleet fleet = fleetManager.getFleet(selectedIndex);
            parentTabbedPane.addTab(fleet.getName(), new FleetManagementPanel(fleet));
            parentTabbedPane.setSelectedIndex(parentTabbedPane.getTabCount() - 1);
        }
    }

    private void updateFleetsList() {
        DefaultListModel<String> model = new DefaultListModel<>();
        fleetManager.getAllFleets().forEach(f -> model.addElement(f.getFleetName()));
        fleetsList.setModel(model);
    }
}