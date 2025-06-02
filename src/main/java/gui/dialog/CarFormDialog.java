package gui.dialog;

import models.TaxiFleet;
import models.cars.ElectricCar;
import models.cars.GasCar;
import gui.panel.CarListPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CarFormDialog extends JDialog {
    // Logger для класу
    private static final Logger logger = LogManager.getLogger(CarFormDialog.class);

    // Основні поля класу
    private TaxiFleet taxiFleet;
    private CarListPanel carListPanel;

    // Компоненти форми
    JTextField makeField;
    JTextField modelField;
    JTextField priceField;
    JTextField speedField;
    JTextField consumptionField;
    JComboBox<String> typeComboBox;
    JComboBox<String> fuelTypeComboBox;
    JLabel consumptionLabel; // Мітка для витрати, тепер поле класу

    // Константи для кольорів
    private final Color BACKGROUND_COLOR = new Color(240, 240, 245);
    private final Color HEADER_COLOR = new Color(60, 90, 153);
    private final Color BUTTON_COLOR = new Color(72, 116, 200);
    private final Color TEXT_COLOR = new Color(50, 50, 50);

    // Шрифти
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    private final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 13);
    private final Font FIELD_FONT = new Font("Arial", Font.PLAIN, 13);

    /**
     * Конструктор для діалогу додавання нового автомобіля
     *
     * @param parent батьківський фрейм
     * @param taxiFleet об'єкт автопарку
     * @param carListPanel панель списку автомобілів для оновлення
     */
    public CarFormDialog(JFrame parent, TaxiFleet taxiFleet, CarListPanel carListPanel) {
        super(parent, "Додати новий автомобіль", true);
        logger.info("Creating CarFormDialog for fleet: {}", taxiFleet.getName());

        this.taxiFleet = taxiFleet;
        this.carListPanel = carListPanel;

        initComponents();

        // Налаштування діалогового вікна
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    /**
     * Ініціалізація всіх компонентів діалогу
     */
    private void initComponents() {
        // Встановлення загальних властивостей панелі
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Заголовок форми
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Панель з формою
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Панель з кнопками
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Створює панель заголовка
     *
     * @return панель заголовка
     */
    JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(HEADER_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("Додати нове авто");
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(Color.WHITE);
        panel.add(headerLabel);

        return panel;
    }

    /**
     * Створює панель з полями форми
     *
     * @return панель форми
     */
    JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 0),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                )
        ));

        // Налаштування для міток
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(8, 5, 8, 15);

        // Налаштування для полів
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.insets = new Insets(8, 5, 8, 5);

        // Марка
        JLabel makeLabel = createFormLabel("Марка:");
        labelConstraints.gridy = 0;
        panel.add(makeLabel, labelConstraints);

        makeField = createFormTextField();
        makeField.setName("makeField"); // Ім'я для тестування
        fieldConstraints.gridy = 0;
        panel.add(makeField, fieldConstraints);

        // Модель
        JLabel modelLabel = createFormLabel("Модель:");
        labelConstraints.gridy = 1;
        panel.add(modelLabel, labelConstraints);

        modelField = createFormTextField();
        modelField.setName("modelField"); // Ім'я для тестування
        fieldConstraints.gridy = 1;
        panel.add(modelField, fieldConstraints);

        // Тип авто
        JLabel typeLabel = createFormLabel("Тип авто:");
        labelConstraints.gridy = 2;
        panel.add(typeLabel, labelConstraints);

        typeComboBox = new JComboBox<>(new String[]{"Бензин/Дизель", "Електричний"});
        typeComboBox.setFont(FIELD_FONT);
        typeComboBox.setName("typeComboBox"); // Ім'я для тестування
        typeComboBox.addActionListener(this::updateFuelTypeVisibility);
        fieldConstraints.gridy = 2;
        panel.add(typeComboBox, fieldConstraints);

        // Тип палива
        JLabel fuelTypeLabel = createFormLabel("Тип палива:");
        labelConstraints.gridy = 3;
        panel.add(fuelTypeLabel, labelConstraints);

        fuelTypeComboBox = new JComboBox<>(new String[]{"Бензин", "Дизель"});
        fuelTypeComboBox.setFont(FIELD_FONT);
        fuelTypeComboBox.setName("fuelTypeComboBox"); // Ім'я для тестування
        fieldConstraints.gridy = 3;
        panel.add(fuelTypeComboBox, fieldConstraints);

        // Ціна
        JLabel priceLabel = createFormLabel("Ціна ($):");
        labelConstraints.gridy = 4;
        panel.add(priceLabel, labelConstraints);

        priceField = createFormTextField();
        priceField.setName("priceField"); // Ім'я для тестування
        fieldConstraints.gridy = 4;
        panel.add(priceField, fieldConstraints);

        // Макс. швидкість
        JLabel speedLabel = createFormLabel("Макс. швидкість (км/год):");
        labelConstraints.gridy = 5;
        panel.add(speedLabel, labelConstraints);

        speedField = createFormTextField();
        speedField.setName("speedField"); // Ім'я для тестування
        fieldConstraints.gridy = 5;
        panel.add(speedField, fieldConstraints);

        // Витрата
        // Ініціалізуємо поле класу consumptionLabel
        this.consumptionLabel = createFormLabel("Витрата (л/100км):"); // Початковий текст
        this.consumptionLabel.setName("consumptionLabel"); // Ім'я для тестування
        labelConstraints.gridy = 6;
        panel.add(this.consumptionLabel, labelConstraints);

        consumptionField = createFormTextField();
        consumptionField.setName("consumptionField"); // Ім'я для тестування
        fieldConstraints.gridy = 6;
        panel.add(consumptionField, fieldConstraints);

        updateFuelTypeVisibility(null); // Викликаємо для встановлення коректного стану при запуску
        return panel;
    }

    /**
     * Створює стандартну мітку форми
     *
     * @param text текст мітки
     * @return налаштована мітка
     */
    JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    /**
     * Створює стандартне текстове поле
     *
     * @return налаштоване текстове поле
     */
    JTextField createFormTextField() {
        JTextField field = new JTextField();
        field.setFont(FIELD_FONT);
        field.setPreferredSize(new Dimension(200, 30));
        return field;
    }

    /**
     * Створює панель з кнопками
     *
     * @return панель кнопок
     */
    JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Кнопка очищення
        JButton clearButton = new JButton("Очистити");
        clearButton.setFont(LABEL_FONT);
        clearButton.setName("clearButton"); // Ім'я для тестування
        clearButton.addActionListener(e -> clearForm());

        // Кнопка скасування
        JButton cancelButton = new JButton("Скасувати");
        cancelButton.setFont(LABEL_FONT);
        cancelButton.setName("cancelButton"); // Ім'я для тестування
        cancelButton.addActionListener(e -> dispose());

        // Кнопка додавання
        JButton addButton = new JButton("Додати авто");
        addButton.setFont(LABEL_FONT);
        addButton.setBackground(BUTTON_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setName("addButton"); // Ім'я для тестування
        addButton.addActionListener(this::addCar);

        panel.add(clearButton);
        panel.add(cancelButton);
        panel.add(addButton);

        return panel;
    }

    /**
     * Оновлює видимість поля вибору типу палива та текст мітки витрати
     * в залежності від типу автомобіля.
     *
     * @param e подія дії (може бути null)
     */
    void updateFuelTypeVisibility(ActionEvent e) {
        if (typeComboBox == null || fuelTypeComboBox == null || consumptionLabel == null) {
            return; // Компоненти ще не ініціалізовані
        }

        boolean isElectric = typeComboBox.getSelectedIndex() == 1;
        fuelTypeComboBox.setVisible(!isElectric);
        consumptionLabel.setText(isElectric ? "Витрата (кВт·год/100км):" : "Витрата (л/100км):");
    }

    /**
     * Додає новий автомобіль до автопарку на основі введених даних
     *
     * @param e подія дії
     */
    void addCar(ActionEvent e) {
        try {
            String make = makeField.getText();
            String model = modelField.getText();
            logger.debug("Attempting to add new car: {} {}", make, model);

            // Перевірка на пусті поля
            if (make.trim().isEmpty() || model.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Марка та модель не можуть бути порожніми",
                        "Помилка введення",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            double price = Double.parseDouble(priceField.getText());
            double speed = Double.parseDouble(speedField.getText());
            double consumption = Double.parseDouble(consumptionField.getText());


            if (typeComboBox.getSelectedIndex() == 1) { // Електричний
                taxiFleet.addCar(new ElectricCar(make, model, price, speed, consumption));
                logger.info("Added new electric car: {} {}", make, model);
            } else { // Бензин/Дизель
                String fuelType = (String) fuelTypeComboBox.getSelectedItem();
                taxiFleet.addCar(new GasCar(make, model, price, speed, consumption, fuelType));
                logger.info("Added new gas car: {} {}, fuel type: {}", make, model, fuelType);
            }

            carListPanel.loadAndSortCars(); // Оновлюємо список в головному вікні
            dispose(); // Закриваємо діалог

            logger.info("Successfully added new car to fleet: {}", taxiFleet.getName());

            // Покращене повідомлення про успіх
            JOptionPane.showMessageDialog(this.getParent(), // Показуємо відносно батьківського фрейму
                    "Авто " + make + " " + model + " успішно додано до автопарку!",
                    "Успішне додавання",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            logger.error("Failed to add car - invalid number format", ex);

            JOptionPane.showMessageDialog(this,
                    "Будь ласка, введіть коректні числові значення для ціни, швидкості та витрати",
                    "Помилка введення",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Очищає всі поля форми
     */
    void clearForm() {
        logger.debug("Clearing car form");
        makeField.setText("");
        modelField.setText("");
        priceField.setText("");
        speedField.setText("");
        consumptionField.setText("");
        typeComboBox.setSelectedIndex(0); // Скидаємо на "Бензин/Дизель"
        // fuelTypeComboBox скинеться автоматично або його видимість/значення оновиться через updateFuelTypeVisibility
        // updateFuelTypeVisibility(null); // Можна викликати явно, якщо є сумніви, але addActionListener на typeComboBox має це зробити
    }
}