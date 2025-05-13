package gui;

import models.TaxiFleet;
import models.cars.Car;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SearchPanel extends JPanel {
    private TaxiFleet fleet;
    private JTable resultTable;

    public SearchPanel(TaxiFleet fleet) {
        this.fleet = fleet;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Панель параметрів пошуку
        JPanel searchPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        JLabel speedLabel = new JLabel("Діапазон швидкості:");
        JTextField minSpeedField = new JTextField();
        JTextField maxSpeedField = new JTextField();

        JButton searchBtn = new JButton("Знайти");
        searchBtn.addActionListener(e -> {
            try {
                double minSpeed = minSpeedField.getText().isEmpty() ? 0 : Double.parseDouble(minSpeedField.getText());
                double maxSpeed = maxSpeedField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxSpeedField.getText());

                List<Car> results = fleet.findCarsBySpeedRange(minSpeed, maxSpeed);
                displayResults(results);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Невірний формат швидкості", "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        });

        searchPanel.add(speedLabel);
        searchPanel.add(new JLabel());
        searchPanel.add(new JLabel("Від:"));
        searchPanel.add(minSpeedField);
        searchPanel.add(new JLabel("До:"));
        searchPanel.add(maxSpeedField);
        searchPanel.add(new JLabel());
        searchPanel.add(searchBtn);

        add(searchPanel, BorderLayout.NORTH);

        // Таблиця результатів
        resultTable = new JTable();
        add(new JScrollPane(resultTable), BorderLayout.CENTER);
    }

    private void displayResults(List<Car> cars) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Марка", "Модель", "Тип", "Швидкість", "Витрата"});

        for (Car car : cars) {
            model.addRow(new Object[]{
                    car.getMake(),
                    car.getModel(),
                    car.getFuelType(),
                    car.getMaxSpeed(),
                    car.getFuelConsumption()
            });
        }

        resultTable.setModel(model);
    }
}