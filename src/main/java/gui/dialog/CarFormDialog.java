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
    private static final Logger logger = LogManager.getLogger(CarFormDialog.class);
    private TaxiFleet taxiFleet;
    private CarListPanel carListPanel;

    private JTextField makeField, modelField, priceField, speedField, consumptionField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> fuelTypeComboBox;

    // Константи для кольорів
    private final Color BACKGROUND_COLOR = new Color(240, 240, 245);
    private final Color HEADER_COLOR = new Color(60, 90, 153);
    private final Color BUTTON_COLOR = new Color(72, 116, 200);
    private final Color TEXT_COLOR = new Color(50, 50, 50);

    // Шрифти
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    private final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 13);
    private final Font FIELD_FONT = new Font("Arial", Font.PLAIN, 13);

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

    private JPanel createHeaderPanel() {
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

    private JPanel createFormPanel() {
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

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(8, 5, 8, 15);

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
        fieldConstraints.gridy = 0;
        panel.add(makeField, fieldConstraints);

        // Модель
        JLabel modelLabel = createFormLabel("Модель:");
        labelConstraints.gridy = 1;
        panel.add(modelLabel, labelConstraints);

        modelField = createFormTextField();
        fieldConstraints.gridy = 1;
        panel.add(modelField, fieldConstraints);

        // Тип авто
        JLabel typeLabel = createFormLabel("Тип авто:");
        labelConstraints.gridy = 2;
        panel.add(typeLabel, labelConstraints);

        typeComboBox = new JComboBox<>(new String[]{"Бензин/Дизель", "Електричний"});
        typeComboBox.setFont(FIELD_FONT);
        typeComboBox.addActionListener(this::updateFuelTypeVisibility);
        fieldConstraints.gridy = 2;
        panel.add(typeComboBox, fieldConstraints);

        // Тип палива
        JLabel fuelTypeLabel = createFormLabel("Тип палива:");
        labelConstraints.gridy = 3;
        panel.add(fuelTypeLabel, labelConstraints);

        fuelTypeComboBox = new JComboBox<>(new String[]{"Бензин", "Дизель"});
        fuelTypeComboBox.setFont(FIELD_FONT);
        fieldConstraints.gridy = 3;
        panel.add(fuelTypeComboBox, fieldConstraints);

        // Ціна
        JLabel priceLabel = createFormLabel("Ціна ($):");
        labelConstraints.gridy = 4;
        panel.add(priceLabel, labelConstraints);

        priceField = createFormTextField();
        fieldConstraints.gridy = 4;
        panel.add(priceField, fieldConstraints);

        // Макс. швидкість
        JLabel speedLabel = createFormLabel("Макс. швидкість (км/год):");
        labelConstraints.gridy = 5;
        panel.add(speedLabel, labelConstraints);

        speedField = createFormTextField();
        fieldConstraints.gridy = 5;
        panel.add(speedField, fieldConstraints);

        // Витрата
        JLabel consumptionLabel = createFormLabel("Витрата:");
        labelConstraints.gridy = 6;
        panel.add(consumptionLabel, labelConstraints);

        consumptionField = createFormTextField();
        fieldConstraints.gridy = 6;
        panel.add(consumptionField, fieldConstraints);

        updateFuelTypeVisibility(null);
        return panel;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createFormTextField() {
        JTextField field = new JTextField();
        field.setFont(FIELD_FONT);
        field.setPreferredSize(new Dimension(200, 30));
        return field;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton clearButton = new JButton("Очистити");
        clearButton.setFont(LABEL_FONT);
        clearButton.addActionListener(e -> clearForm());

        JButton cancelButton = new JButton("Скасувати");
        cancelButton.setFont(LABEL_FONT);
        cancelButton.addActionListener(e -> dispose());

        JButton addButton = new JButton("Додати авто");
        addButton.setFont(LABEL_FONT);
        addButton.setBackground(BUTTON_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(this::addCar);

        panel.add(clearButton);
        panel.add(cancelButton);
        panel.add(addButton);

        return panel;
    }

    private void updateFuelTypeVisibility(ActionEvent e) {
        if (typeComboBox == null || fuelTypeComboBox == null) return;

        boolean isElectric = typeComboBox.getSelectedIndex() == 1;
        fuelTypeComboBox.setVisible(!isElectric);

        // Оновлюємо текст поля з витратою палива
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                updateConsumptionLabel((JPanel)component, isElectric);
            }
        }
    }

    private void updateConsumptionLabel(JPanel panel, boolean isElectric) {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JLabel &&
                    ((JLabel)component).getText().startsWith("Витрата")) {
                ((JLabel)component).setText(isElectric ? "Витрата (кВт·год/100км):" : "Витрата (л/100км):");
                break;
            } else if (component instanceof JPanel) {
                // Рекурсивно перевіряємо вкладені панелі
                updateConsumptionLabel((JPanel)component, isElectric);
            }
        }
    }

    private void addCar(ActionEvent e) {
        try {
            String make = makeField.getText();
            String model = modelField.getText();
            logger.debug("Attempting to add new car: {} {}", make, model);

            double price = Double.parseDouble(priceField.getText());
            double speed = Double.parseDouble(speedField.getText());
            double consumption = Double.parseDouble(consumptionField.getText());

            // Перевірка на пусті поля
            if (make.trim().isEmpty() || model.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Марка та модель не можуть бути порожніми",
                        "Помилка введення",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (typeComboBox.getSelectedIndex() == 1) { // Електричний
                taxiFleet.addCar(new ElectricCar(make, model, price, speed, consumption));
                logger.info("Added new electric car: {} {}", make, model);
            } else { // Бензин/Дизель
                String fuelType = (String) fuelTypeComboBox.getSelectedItem();
                taxiFleet.addCar(new GasCar(make, model, price, speed, consumption, fuelType));
                logger.info("Added new gas car: {} {}, fuel type: {}", make, model, fuelType);
            }

            carListPanel.loadAndSortCars();
            dispose();

            logger.info("Successfully added new car to fleet: {}", taxiFleet.getName());

            // Покращене повідомлення про успіх
            JOptionPane.showMessageDialog(this.getParent(),
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

    private void clearForm() {
        logger.debug("Clearing car form");
        makeField.setText("");
        modelField.setText("");
        priceField.setText("");
        speedField.setText("");
        consumptionField.setText("");
        typeComboBox.setSelectedIndex(0);
    }
}