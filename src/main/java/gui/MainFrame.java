package gui;

import gui.panel.FleetsManagementPanel;
import models.TaxiFleetManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

/**
 * Головний клас, який відповідає за створення та відображення основного вікна програми для керування таксопарками.
 * Використовує систему вкладок для організації різних функціональних модулів.
 */
public class MainFrame extends JFrame {
    /** Логер для запису інформації про роботу класу */
    private static final Logger logger = LogManager.getLogger(MainFrame.class);

    /** Менеджер для управління таксопарками */
    private TaxiFleetManager fleetManager;

    /** Система вкладок для розміщення різних функціональних панелей */
    private JTabbedPane mainTabbedPane;

    /**
     * Конструктор, який створює та ініціалізує головне вікно програми.
     * Налаштовує базові параметри вікна та ініціює створення користувацького інтерфейсу.
     */
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

    /**
     * Ініціалізує користувацький інтерфейс.
     * Налаштовує розміри вікна, створює систему вкладок та додає панелі управління.
     */
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