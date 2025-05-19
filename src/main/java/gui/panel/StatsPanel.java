package gui.panel;

import models.TaxiFleet;
import models.cars.Car;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.CarStatsService;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Панель для відображення статистики таксопарку у вигляді графіків та текстової інформації.
 * Відображає розподіл автомобілів, загальну статистику та дані про витрати палива.
 */
public class StatsPanel extends JPanel {
    // Логер для реєстрації подій
    private static final Logger logger = LogManager.getLogger(StatsPanel.class);

    // Посилання на об'єкт таксопарку
    private TaxiFleet taxiFleet;

    private final CarStatsService carStatsService;

    // Константи кольорів для UI елементів
    private static final Color HEADER_COLOR = new Color(52, 73, 94);
    static final Color ELECTRIC_COLOR = new Color(46, 204, 113);
    static final Color GAS_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    // Константи шрифтів для різних елементів UI
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 12);

    /**
     * Конструктор, створює панель статистики для вказаного таксопарку.
     *
     * @param taxiFleet Об'єкт таксопарку, для якого відображається статистика
     */
    public StatsPanel(TaxiFleet taxiFleet) {
        logger.info("Creating StatsPanel for fleet: {}", taxiFleet.getName());
        this.taxiFleet = taxiFleet;
        this.carStatsService = new CarStatsService(taxiFleet);
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Панель з заголовком
        JPanel headerPanel = createHeaderPanel();

        // Основна панель з даними
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Ліва панель з текстовою інформацією
        JPanel textPanel = createTextInfoPanel();

        // Права панель з діаграмами
        JPanel chartsPanel = createChartsPanel();

        mainPanel.add(textPanel);
        mainPanel.add(chartsPanel);

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Створює панель заголовка з назвою таксопарку.
     *
     * @return JPanel з заголовком
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(HEADER_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(getWidth(), 70));

        JLabel titleLabel = new JLabel("Статистика таксопарку '" + taxiFleet.getName() + "'");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        panel.add(titleLabel);
        return panel;
    }

    /**
     * Створює текстову інформаційну панель з даними про парк.
     *
     * @return JPanel з текстовою інформацією
     */
    JPanel createTextInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Обчислення статистики
        int totalCars = taxiFleet.getCars().size();
        int electricCount = taxiFleet.getElectricCarCount();
        int gasCount = taxiFleet.getGasCarCount();
        double totalCost = taxiFleet.calculateTotalCost();

        // Розрахунок середньої витрати окремо для електро та паливних авто
        double avgElectricConsumption = carStatsService.calculateAverageElectricConsumption();
        double avgGasConsumption = carStatsService.calculateAverageGasConsumption();

        // Додаємо інформацію про кількість авто
        addSectionHeader(panel, "Загальна інформація");
        addKeyValueRow(panel, "Кількість автомобілів:", totalCars + " шт.");
        addKeyValueRow(panel, "- Електричні:", electricCount + " шт.");
        addKeyValueRow(panel, "- Бензин/Дизель:", gasCount + " шт.");
        addSeparator(panel);

        // Додаємо інформацію про вартість
        addSectionHeader(panel, "Фінансова інформація");
        addKeyValueRow(panel, "Загальна вартість парку:", String.format("%.2f $", totalCost));
        addKeyValueRow(panel, "Середня вартість авто:", String.format("%.2f $",
                totalCars > 0 ? totalCost / totalCars : 0));
        addSeparator(panel);

        // Додаємо інформацію про витрату палива
        addSectionHeader(panel, "Витрата пального");
        if (electricCount > 0) {
            addKeyValueRow(panel, "Середня витрата електроенергії:",
                    String.format("%.2f кВт·год/100км", avgElectricConsumption));
        }
        if (gasCount > 0) {
            addKeyValueRow(panel, "Середня витрата палива:",
                    String.format("%.2f л/100км", avgGasConsumption));
        }

        // Додаємо пружину, щоб заповнити простір
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    /**
     * Створює панель з діаграмами для візуального відображення статистики.
     *
     * @return JPanel з діаграмами
     */
    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 15));
        panel.setBackground(BACKGROUND_COLOR);

        // Верхня діаграма - розподіл типів авто
        JPanel pieChartPanel = new JPanel(new BorderLayout());
        pieChartPanel.setBackground(Color.WHITE);
        pieChartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel pieChartTitle = new JLabel("Розподіл автомобілів за типом", JLabel.CENTER);
        pieChartTitle.setFont(HEADER_FONT);
        pieChartTitle.setForeground(TEXT_COLOR);

        PieChartPanel pieChart = new PieChartPanel();

        pieChartPanel.add(pieChartTitle, BorderLayout.NORTH);
        pieChartPanel.add(pieChart, BorderLayout.CENTER);

        // Нижня частина - два графіка економічності
        JPanel chartsBottomPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        chartsBottomPanel.setBackground(BACKGROUND_COLOR);

        // Графік для електромобілів
        JPanel electricBarPanel = new JPanel(new BorderLayout());
        electricBarPanel.setBackground(Color.WHITE);
        electricBarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel electricChartTitle = new JLabel("Витрата електрики (кВт·год/100км)", JLabel.CENTER);
        electricChartTitle.setFont(HEADER_FONT);
        electricChartTitle.setForeground(TEXT_COLOR);

        FuelConsumptionBarChart electricChart = new FuelConsumptionBarChart(true);

        electricBarPanel.add(electricChartTitle, BorderLayout.NORTH);
        electricBarPanel.add(electricChart, BorderLayout.CENTER);

        // Графік для паливних авто
        JPanel gasBarPanel = new JPanel(new BorderLayout());
        gasBarPanel.setBackground(Color.WHITE);
        gasBarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel gasChartTitle = new JLabel("Витрата палива (л/100км)", JLabel.CENTER);
        gasChartTitle.setFont(HEADER_FONT);
        gasChartTitle.setForeground(TEXT_COLOR);

        FuelConsumptionBarChart gasChart = new FuelConsumptionBarChart(false);

        gasBarPanel.add(gasChartTitle, BorderLayout.NORTH);
        gasBarPanel.add(gasChart, BorderLayout.CENTER);

        chartsBottomPanel.add(electricBarPanel);
        chartsBottomPanel.add(gasBarPanel);

        panel.add(pieChartPanel);
        panel.add(chartsBottomPanel);

        return panel;
    }

    /**
     * Додає заголовок секції до панелі.
     *
     * @param panel Панель, до якої додається заголовок
     * @param text  Текст заголовка
     */
    private void addSectionHeader(JPanel panel, String text) {
        JLabel header = new JLabel(text);
        header.setFont(HEADER_FONT);
        header.setForeground(TEXT_COLOR);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(header);
        panel.add(Box.createVerticalStrut(5));
    }

    /**
     * Додає рядок з парою ключ-значення до панелі.
     *
     * @param panel Панель, до якої додається рядок
     * @param key   Ключ (назва параметра)
     * @param value Значення параметра
     */
    private void addKeyValueRow(JPanel panel, String key, String value) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel keyLabel = new JLabel(key);
        keyLabel.setFont(REGULAR_FONT);
        keyLabel.setForeground(TEXT_COLOR);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(REGULAR_FONT);
        valueLabel.setForeground(TEXT_COLOR);

        rowPanel.add(keyLabel);
        rowPanel.add(Box.createHorizontalGlue());
        rowPanel.add(valueLabel);

        panel.add(rowPanel);
        panel.add(Box.createVerticalStrut(3));
    }

    /**
     * Додає горизонтальний розділювач до панелі.
     *
     * @param panel Панель, до якої додається розділювач
     */
    private void addSeparator(JPanel panel) {
        panel.add(Box.createVerticalStrut(5));
        JSeparator separator = new JSeparator();
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(separator);
        panel.add(Box.createVerticalStrut(10));
    }

    /**
     * Внутрішній клас для кругової діаграми, яка показує розподіл типів автомобілів.
     */
    class PieChartPanel extends JPanel {
        /**
         * Конструктор панелі кругової діаграми.
         */
        public PieChartPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            logger.debug("Painting pie chart for fleet: {}", taxiFleet.getName());
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int pieSize = Math.min(width, height) - 60;
            int x = (width - pieSize) / 2;
            int y = (height - pieSize) / 2;

            // Обчислення даних
            int electricCount = taxiFleet.getElectricCarCount();
            int gasCount = taxiFleet.getGasCarCount();
            int total = electricCount + gasCount;

            if (total > 0) {
                double electricAngle = 360.0 * electricCount / total;
                double gasAngle = 360.0 * gasCount / total;

                // Малюємо електричну частину
                g2.setColor(ELECTRIC_COLOR);
                g2.fillArc(x, y, pieSize, pieSize, 0, (int) electricAngle);

                // Малюємо газову частину
                g2.setColor(GAS_COLOR);
                g2.fillArc(x, y, pieSize, pieSize, (int) electricAngle, (int) gasAngle);

                // Малюємо легенду
                int legendX = width - 150;
                int legendY = height - 60;

                // Електричні авто
                g2.setColor(ELECTRIC_COLOR);
                g2.fillRect(legendX, legendY, 15, 15);
                g2.setColor(TEXT_COLOR);
                g2.setFont(REGULAR_FONT);
                g2.drawString("Електричні: " + electricCount, legendX + 20, legendY + 12);

                // Бензинові авто
                g2.setColor(GAS_COLOR);
                g2.fillRect(legendX, legendY + 20, 15, 15);
                g2.setColor(TEXT_COLOR);
                g2.drawString("Бензин/Дизель: " + gasCount, legendX + 20, legendY + 32);
            } else {
                g2.setColor(TEXT_COLOR);
                g2.setFont(REGULAR_FONT);
                g2.drawString("Немає даних для відображення", width / 3, height / 2);
            }
        }
    }

    /**
     * Внутрішній клас для стовпчикової діаграми витрат палива.
     */
    class FuelConsumptionBarChart extends JPanel {
        // Прапорець, що вказує тип діаграми: true - електромобілі, false - паливні авто
        private boolean isElectric;

        /**
         * Конструктор діаграми витрат палива.
         *
         * @param isElectric true - для електромобілів, false - для паливних авто
         */
        public FuelConsumptionBarChart(boolean isElectric) {
            setBackground(Color.WHITE);
            this.isElectric = isElectric;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            logger.debug("Painting {} consumption chart for fleet: {}",
                    isElectric ? "electric" : "gas", taxiFleet.getName());
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int chartHeight = height - 30;
            int chartWidth = width - 60;
            int startX = 50;
            int startY = height - 30;

            // Отримуємо авто відповідного типу
            List<Car> filteredCars = taxiFleet.getCars().stream()
                    .filter(c -> isElectric ? "Електричний".equals(c.getFuelType()) : !"Електричний".equals(c.getFuelType()))
                    .sorted(Comparator.comparingDouble(Car::getFuelConsumption))
                    .collect(Collectors.toList());

            int carsToShow = Math.min(5, filteredCars.size());

            if (carsToShow > 0) {
                // Знаходимо максимальне значення витрат для масштабування
                double maxConsumption = 0;
                for (int i = 0; i < carsToShow; i++) {
                    double consumption = filteredCars.get(i).getFuelConsumption();
                    if (consumption > maxConsumption) {
                        maxConsumption = consumption;
                    }
                }

                // Якщо немає даних про витрати
                if (maxConsumption == 0) {
                    g2.setColor(TEXT_COLOR);
                    g2.setFont(REGULAR_FONT);
                    g2.drawString("Немає даних про витрати", width / 3, height / 2);
                    return;
                }

                // Масштабний коефіцієнт
                double scale = chartHeight / (maxConsumption * 1.2);
                int barWidth = chartWidth / (carsToShow * 2);

                // Малюємо осі
                g2.setColor(TEXT_COLOR);
                g2.drawLine(startX, startY, startX + chartWidth, startY); // x-axis
                g2.drawLine(startX, startY, startX, startY - chartHeight); // y-axis

                // Малюємо стовпці
                for (int i = 0; i < carsToShow; i++) {
                    Car car = filteredCars.get(i);
                    double consumption = car.getFuelConsumption();
                    int barHeight = (int) (consumption * scale);
                    int barX = startX + i * (chartWidth / carsToShow) + (chartWidth / carsToShow - barWidth) / 2;
                    int barY = startY - barHeight;

                    // Колір залежно від типу діаграми
                    g2.setColor(isElectric ? ELECTRIC_COLOR : GAS_COLOR);

                    // Малюємо стовпець
                    g2.fillRect(barX, barY, barWidth, barHeight);

                    // Додаємо значення витрат
                    g2.setColor(TEXT_COLOR);
                    g2.setFont(new Font("Arial", Font.PLAIN, 10));
                    String consumptionText = String.format("%.1f", consumption);
                    int textWidth = g2.getFontMetrics().stringWidth(consumptionText);
                    g2.drawString(consumptionText, barX + (barWidth - textWidth) / 2, barY - 5);

                    // Додаємо назву авто
                    String carName = car.getMake() + " " + car.getModel();
                    if (carName.length() > 10) {
                        carName = carName.substring(0, 8) + "...";
                    }

                    // Повертаємо назву авто на 90 градусів
                    AffineTransform oldTransform = g2.getTransform();
                    g2.rotate(-Math.PI / 2);
                    g2.drawString(carName, -startY + 5, barX + barWidth / 2 + 3);
                    g2.setTransform(oldTransform);
                }

                // Додаємо одиниці виміру
                g2.setColor(TEXT_COLOR);
                g2.setFont(new Font("Arial", Font.ITALIC, 10));
                String units = isElectric ? "кВт·год/100км" : "л/100км";
                g2.drawString(units, startX - 40, startY - chartHeight - 5);
            } else {
                g2.setColor(TEXT_COLOR);
                g2.setFont(REGULAR_FONT);
                String message = isElectric ? "Немає електромобілів" : "Немає паливних авто";
                g2.drawString(message, width / 3, height / 2);
            }
        }
    }
}