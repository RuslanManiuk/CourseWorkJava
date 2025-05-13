package gui;

import models.TaxiFleetManager;

import javax.swing.*;
import java.awt.*;

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
        setSize(1000, 700);
        setLocationRelativeTo(null);

        mainTabbedPane = new JTabbedPane();

        // Додаємо панель керування таксопарками
        mainTabbedPane.addTab("Таксопарки", new FleetsManagementPanel(fleetManager, mainTabbedPane));

        add(mainTabbedPane);
    }
}