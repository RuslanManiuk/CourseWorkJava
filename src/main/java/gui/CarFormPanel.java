package gui;

import models.TaxiFleet;
import models.cars.ElectricCar;
import models.cars.GasCar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CarFormPanel extends JPanel {
    private TaxiFleet taxiFleet;
    private CarListPanel carListPanel;

    private JTextField makeField, modelField, priceField, speedField, consumptionField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> fuelTypeComboBox;

    public CarFormPanel(TaxiFleet taxiFleet, CarListPanel carListPanel) {
        this.taxiFleet = taxiFleet;
        this.carListPanel = carListPanel;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(0, 2, 5, 5));

        // Поля форми
        add(new JLabel("Марка:"));
        makeField = new JTextField();
        add(makeField);

        add(new JLabel("Модель:"));
        modelField = new JTextField();
        add(modelField);

        add(new JLabel("Тип авто:"));
        typeComboBox = new JComboBox<>(new String[]{"Бензин/Дизель", "Електричний"});
        typeComboBox.addActionListener(this::updateFuelTypeVisibility);
        add(typeComboBox);

        add(new JLabel("Тип палива:"));
        fuelTypeComboBox = new JComboBox<>(new String[]{"Бензин", "Дизель"});
        add(fuelTypeComboBox);

        add(new JLabel("Ціна ($):"));
        priceField = new JTextField();
        add(priceField);

        add(new JLabel("Макс. швидкість:"));
        speedField = new JTextField();
        add(speedField);

        add(new JLabel("Витрата:"));
        consumptionField = new JTextField();
        add(consumptionField);

        // Кнопка додавання
        JButton addButton = new JButton("Додати авто");
        addButton.addActionListener(this::addCar);
        add(new JLabel());
        add(addButton);

        updateFuelTypeVisibility(null);
    }

    private void updateFuelTypeVisibility(ActionEvent e) {
        boolean isElectric = typeComboBox.getSelectedIndex() == 1;
        fuelTypeComboBox.setVisible(!isElectric);
        ((JLabel) getComponent(6)).setText(isElectric ? "Витрата (кВт·год):" : "Витрата (л):");
    }

    private void addCar(ActionEvent e) {
        try {
            String make = makeField.getText();
            String model = modelField.getText();
            double price = Double.parseDouble(priceField.getText());
            double speed = Double.parseDouble(speedField.getText());
            double consumption = Double.parseDouble(consumptionField.getText());

            if (typeComboBox.getSelectedIndex() == 1) { // Електричний
                taxiFleet.addCar(new ElectricCar(make, model, price, speed, consumption));
            } else { // Бензин/Дизель
                String fuelType = (String) fuelTypeComboBox.getSelectedItem();
                taxiFleet.addCar(new GasCar(make, model, price, speed, consumption, fuelType));
            }

            carListPanel.loadCars();
            clearForm();
            JOptionPane.showMessageDialog(this, "Авто додано успішно!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Невірний формат числа", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        makeField.setText("");
        modelField.setText("");
        priceField.setText("");
        speedField.setText("");
        consumptionField.setText("");
    }
}