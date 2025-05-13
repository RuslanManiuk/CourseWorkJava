package gui.panel;

import models.TaxiFleet;

import javax.swing.*;
import java.awt.*;

public class FleetManagementPanel extends JPanel {
    private TaxiFleet fleet;
    private JTabbedPane tabbedPane;

    public FleetManagementPanel(TaxiFleet fleet) {
        this.fleet = fleet;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        CarListPanel carListPanel = new CarListPanel(fleet);

        // Додаємо всі необхідні панелі
        tabbedPane.addTab("Автомобілі", new CarListPanel(fleet));
        tabbedPane.addTab("Додати авто", new CarFormPanel(fleet, carListPanel));
        tabbedPane.addTab("Статистика", new StatsPanel(fleet));

        add(tabbedPane, BorderLayout.CENTER);

        // Кнопка закриття
        JButton closeBtn = new JButton("Закрити таксопарк");
        closeBtn.addActionListener(e -> {
            JTabbedPane parent = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, this);
            if (parent != null) {
                parent.remove(this);
            }
        });
        add(closeBtn, BorderLayout.SOUTH);
    }
}