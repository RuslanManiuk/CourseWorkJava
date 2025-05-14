package gui.panel;

import models.TaxiFleet;
import gui.dialog.CarFormDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class FleetManagementPanel extends JPanel {
    private static final Logger logger = LogManager.getLogger(FleetManagementPanel.class);
    private TaxiFleet fleet;
    private JTabbedPane tabbedPane;
    private CarListPanel carListPanel;

    public FleetManagementPanel(TaxiFleet fleet) {
        logger.info("Creating FleetManagementPanel for fleet: {}", fleet.getName());
        this.fleet = fleet;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        carListPanel = new CarListPanel(fleet);
        tabbedPane.addTab("Автомобілі", carListPanel);
        tabbedPane.addTab("Статистика", new StatsPanel(fleet));

        add(tabbedPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Закрити таксопарк");
        closeBtn.addActionListener(e -> {
            logger.debug("Closing fleet management for: {}", fleet.getName());
            JTabbedPane parent = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, this);
            if (parent != null) {
                parent.remove(this);
                logger.info("Fleet management closed for: {}", fleet.getName());
            }
        });
        add(closeBtn, BorderLayout.SOUTH);
    }
}