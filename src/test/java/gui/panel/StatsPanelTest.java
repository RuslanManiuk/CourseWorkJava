package gui.panel;

import models.TaxiFleet;
import models.cars.Car;
import models.cars.ElectricCar;
import models.cars.GasCar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatsPanelTest {
    private TaxiFleet taxiFleet;
    private StatsPanel statsPanel;

    @BeforeEach
    void setUp() {
        taxiFleet = new TaxiFleet("Test Fleet");
        statsPanel = new StatsPanel(taxiFleet);
    }

    @Test
    void testAddFuelConsumptionInfo_WithElectricAndGasCars() {
        // Додаємо тестові авто
        taxiFleet.getCars().add(new ElectricCar("Tesla", "Model 3", 45000.0, 220.0, 15.5));
        taxiFleet.getCars().add(new GasCar("Toyota", "Camry", 30000.0, 180.0, 8.5, "Бензин"));
        taxiFleet.getCars().add(new GasCar("Volkswagen", "Golf", 25000.0, 190.0, 6.5, "Дизель"));

        // Отримуємо текстову панель
        JPanel textPanel = statsPanel.createTextInfoPanel();

        // Перевіряємо, що панель містить очікувані компоненти
        assertTrue(containsText(textPanel, "Середня витрата електроенергії:"));
        assertTrue(containsText(textPanel, "Середня витрата палива:"));
    }

    @Test
    void testAddFuelConsumptionInfo_WithElectricCarsOnly() {
        // Додаємо тільки електричні авто
        taxiFleet.getCars().add(new ElectricCar("Tesla", "Model 3", 45000.0, 220.0, 15.5));
        taxiFleet.getCars().add(new ElectricCar("Nissan", "Leaf", 32000.0, 180.0, 14.0));

        JPanel textPanel = statsPanel.createTextInfoPanel();

        assertTrue(containsText(textPanel, "Середня витрата електроенергії:"));
        assertFalse(containsText(textPanel, "Середня витрата палива:")); // Не повинно бути
    }

    @Test
    void testAddFuelConsumptionInfo_WithGasCarsOnly() {
        // Додаємо тільки паливні авто
        taxiFleet.getCars().add(new GasCar("Toyota", "Camry", 30000.0, 180.0, 8.5, "Бензин"));
        taxiFleet.getCars().add(new GasCar("Volkswagen", "Golf", 25000.0, 190.0, 6.5, "Дизель"));

        JPanel textPanel = statsPanel.createTextInfoPanel();

        assertFalse(containsText(textPanel, "Середня витрата електроенергії:")); // Не повинно бути
        assertTrue(containsText(textPanel, "Середня витрата палива:"));
    }

    @Test
    void testPieChartPanel_PaintComponent_WithBothTypes() {
        // Додаємо авто обох типів
        taxiFleet.getCars().add(new ElectricCar("Tesla", "Model 3", 45000.0, 220.0, 15.5));
        taxiFleet.getCars().add(new GasCar("Toyota", "Camry", 30000.0, 180.0, 8.5, "Бензин"));

        StatsPanel.PieChartPanel pieChartPanel = statsPanel.new PieChartPanel();
        pieChartPanel.setSize(300, 300);

        // Створюємо справжній BufferedImage для отримання Graphics2D
        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        try {
            // Викликаємо paintComponent
            pieChartPanel.paintComponent(g2);

            // Перевіряємо, що метод виконався без помилок
            // Додаткові перевірки можна додати при необхідності
        } finally {
            g2.dispose();
        }
    }

    @Test
    void testPieChartPanel_PaintComponent_EmptyFleet() {
        // Створюємо тестовий компонент
        StatsPanel.PieChartPanel pieChartPanel = statsPanel.new PieChartPanel();

        // Встановлюємо розміри (важливо для коректної роботи paintComponent)
        pieChartPanel.setSize(300, 300);

        // Створюємо BufferedImage для отримання Graphics об'єкта
        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        try {
            // Налаштовуємо необхідні атрибути
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Викликаємо тестований метод
            pieChartPanel.paintComponent(g2);

            // Якщо потрібно, можна додати додаткові перевірки
            // Наприклад, перевірити, що для порожнього флоту виводиться повідомлення
        } finally {
            // Обов'язково звільняємо ресурси
            g2.dispose();
        }
    }

    @Test
    void testFuelConsumptionBarChart_PaintComponent_Electric() {
        // Add test data
        taxiFleet.getCars().add(new ElectricCar("Tesla", "Model 3", 45000.0, 220.0, 15.5));

        // Create the chart panel
        StatsPanel.FuelConsumptionBarChart chart = statsPanel.new FuelConsumptionBarChart(true);
        chart.setSize(400, 300);

        // Create a real graphics context
        BufferedImage image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        try {
            // Call paintComponent
            chart.paintComponent(g2);

            // Test passed if no exceptions were thrown
        } finally {
            g2.dispose();
        }
    }

    @Test
    void testFuelConsumptionBarChart_PaintComponent_Gas() {
        // Додаємо паливні авто
        taxiFleet.getCars().add(new GasCar("Toyota", "Camry", 30000.0, 180.0, 8.5, "Бензин"));
        taxiFleet.getCars().add(new GasCar("Volkswagen", "Golf", 25000.0, 190.0, 6.5, "Дизель"));

        StatsPanel.FuelConsumptionBarChart chart = statsPanel.new FuelConsumptionBarChart(false);
        chart.setSize(400, 300);

        // Створюємо справжній BufferedImage для отримання Graphics2D
        BufferedImage image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        try {
            chart.paintComponent(g2);

            // Перевіряємо, що використано правильний колір для паливних авто
            // Тут ми не можемо перевірити виклики без мокування, тому просто перевіряємо, що метод виконався без помилок
        } finally {
            g2.dispose();
        }
    }

    @Test
    void testFuelConsumptionBarChart_PaintComponent_RotatedText_RealGraphics() {
        // Add test car
        taxiFleet.getCars().add(new GasCar("Toyota", "Camry", 30000.0, 180.0, 8.5, "Бензин"));

        // Create chart panel
        StatsPanel.FuelConsumptionBarChart chart = statsPanel.new FuelConsumptionBarChart(false);
        chart.setSize(400, 300);

        // Create real graphics context
        BufferedImage image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        try {
            // Call paintComponent
            chart.paintComponent(g2);

            // Can't verify rotation directly, but can check if executed without errors
        } finally {
            g2.dispose();
        }
    }

    // Допоміжний метод для перевірки наявності тексту в панелі
    private boolean containsText(Container container, String text) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().contains(text)) {
                    return true;
                }
            } else if (comp instanceof Container) {
                if (containsText((Container) comp, text)) {
                    return true;
                }
            }
        }
        return false;
    }
}