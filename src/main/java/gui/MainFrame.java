package gui;

import gui.panel.FleetsManagementPanel;
import models.TaxiFleetManager;

import javax.swing.*;

public class MainFrame extends JFrame {
    private TaxiFleetManager fleetManager;
    private JTabbedPane mainTabbedPane;

    public MainFrame() {
        super("Управління таксопарками");
        fleetManager = new TaxiFleetManager();
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 700);
        setLocationRelativeTo(null);

        mainTabbedPane = new JTabbedPane();

        // Додаємо панель керування таксопарками
        mainTabbedPane.addTab("Таксопарки", new FleetsManagementPanel(fleetManager, mainTabbedPane));

        add(mainTabbedPane);
    }
}