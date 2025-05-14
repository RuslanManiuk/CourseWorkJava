package gui.panel;

import models.TaxiFleet;
import gui.dialog.CarFormDialog;

import javax.swing.*;
import java.awt.*;

public class FleetManagementPanel extends JPanel {
    private TaxiFleet fleet;
    private JTabbedPane tabbedPane;
    private CarListPanel carListPanel;

    public FleetManagementPanel(TaxiFleet fleet) {
        this.fleet = fleet;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Створюємо єдиний екземпляр CarListPanel
        carListPanel = new CarListPanel(fleet);

        // Додаємо панель зі списком автомобілів
        tabbedPane.addTab("Автомобілі", carListPanel);

        // Додаємо панель статистики
        tabbedPane.addTab("Статистика", new StatsPanel(fleet));

        // Видаляємо кнопку додавання авто, оскільки вона тепер є в заголовку CarListPanel

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