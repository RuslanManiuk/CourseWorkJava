package gui.panel;

import models.TaxiFleet;
import gui.dialog.CarFormDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Панель для управління таксопарком.
 */
public class FleetManagementPanel extends JPanel {

    // Логер для цього класу
    private static final Logger logger = LogManager.getLogger(FleetManagementPanel.class);

    // Таксопарк, яким керуємо
    private TaxiFleet fleet;

    // UI компоненти
    private JTabbedPane tabbedPane;
    private CarListPanel carListPanel;

    /**
     * Конструктор, що ініціалізує панель управління таксопарком.
     *
     * @param fleet Таксопарк для управління
     */
    public FleetManagementPanel(TaxiFleet fleet) {
        logger.info("Creating FleetManagementPanel for fleet: {}", fleet.getName());
        this.fleet = fleet;
        initComponents();
    }

    /**
     * Ініціалізація та розміщення всіх UI компонентів.
     */
    private void initComponents() {
        // Налаштування макету панелі
        setLayout(new BorderLayout());

        // Створення панелі з вкладками для списку автомобілів та статистики
        tabbedPane = new JTabbedPane();
        carListPanel = new CarListPanel(fleet);
        tabbedPane.addTab("Автомобілі", carListPanel);
        tabbedPane.addTab("Статистика", new StatsPanel(fleet));

        // Додавання панелі з вкладками в центр
        add(tabbedPane, BorderLayout.CENTER);

        // Створення та налаштування кнопки закриття
        JButton closeBtn = new JButton("Закрити таксопарк");
        closeBtn.addActionListener(e -> {
            logger.debug("Closing fleet management for: {}", fleet.getName());
            JTabbedPane parent = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, this);
            if (parent != null) {
                parent.remove(this);
                logger.info("Fleet management closed for: {}", fleet.getName());
            }
        });

        // Додавання кнопки закриття внизу
        add(closeBtn, BorderLayout.SOUTH);
    }
}