package gui;

import models.TaxiFleet;
import models.cars.Car;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CarListPanel extends JPanel {
    private TaxiFleet taxiFleet;
    private JTable carTable;

    public CarListPanel(TaxiFleet taxiFleet) {
        this.taxiFleet = taxiFleet;
        initComponents();
        loadCars();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Модель таблиці
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Марка", "Модель", "Тип", "Ціна", "Швидкість", "Витрата"});

        carTable = new JTable(model);
        add(new JScrollPane(carTable), BorderLayout.CENTER);

        // Кнопки керування
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Оновити");
        JButton removeBtn = new JButton("Видалити");

        refreshBtn.addActionListener(e -> loadCars());
        removeBtn.addActionListener(e -> removeSelectedCar());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(removeBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadCars() {
        DefaultTableModel model = (DefaultTableModel) carTable.getModel();
        model.setRowCount(0); // Очистити таблицю

        for (Car car : taxiFleet.getCars()) {
            model.addRow(new Object[]{
                    car.getMake(),
                    car.getModel(),
                    car.getFuelType(),
                    car.getPrice(),
                    car.getMaxSpeed(),
                    car.getFuelConsumption() + (car.getFuelType().equals("Електричний") ? " кВт·год" : " л")
            });
        }
    }

    private void removeSelectedCar() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow >= 0) {
            Car car = taxiFleet.getCars().get(selectedRow);
            taxiFleet.removeCar(car);
            loadCars();
        } else {
            JOptionPane.showMessageDialog(this, "Виберіть авто для видалення", "Помилка", JOptionPane.WARNING_MESSAGE);
        }
    }
}