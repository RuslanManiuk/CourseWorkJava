package gui;

import gui.panel.FleetsManagementPanel;
import models.TaxiFleetManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

public class MainFrame extends JFrame {
    private static final Logger logger = LogManager.getLogger(MainFrame.class);
    private TaxiFleetManager fleetManager;
    private JTabbedPane mainTabbedPane;

    public MainFrame() {
        super("Управління таксопарками");
        logger.info("Creating MainFrame");
        try {
            fleetManager = new TaxiFleetManager();
            initUI();
            logger.info("MainFrame initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize MainFrame", e);
            throw e;
        }
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