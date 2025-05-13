package models;

import database.DataBaseManager;
import models.cars.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaxiFleetManager {
    private List<TaxiFleet> fleets = new ArrayList<>();
    private DataBaseManager dbManager;

    public TaxiFleetManager() {
        this.dbManager = new DataBaseManager();
        loadFleetsFromDatabase(); // Завантажуємо дані при створенні
    }

    // Додаємо метод для отримання списку таксопарків
    public List<TaxiFleet> getFleets() {
        return fleets;
    }

    // Додаємо метод для додавання таксопарку
    public void addFleet(TaxiFleet fleet) {
        fleets.add(fleet);
        saveFleetToDatabase(fleet);
    }

    // Додаємо метод для видалення таксопарку
    public void removeFleet(TaxiFleet fleet) {
        fleets.remove(fleet);
        try {
            dbManager.executeUpdate("DELETE FROM fleets WHERE fleet_id = ?", fleet.getFleetId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createFleet(String name) {
        TaxiFleet fleet = new TaxiFleet(name);
        fleets.add(fleet);
        saveFleetToDatabase(fleet);
    }

    public TaxiFleet getFleet(int index) {
        if (index >= 0 && index < fleets.size()) {
            return fleets.get(index);
        }
        throw new IndexOutOfBoundsException("Invalid fleet index: " + index);
    }

    private void loadFleetsFromDatabase() {
        try {
            ResultSet rs = dbManager.executeQuery("SELECT * FROM fleets");
            while (rs.next()) {
                TaxiFleet fleet = new TaxiFleet(rs.getString("name"));
                fleet.setFleetId(rs.getInt("fleet_id"));
                loadCarsForFleet(fleet);
                fleets.add(fleet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCarsForFleet(TaxiFleet fleet) {
        try {
            ResultSet rs = dbManager.executeQuery(
                    "SELECT * FROM cars WHERE fleet_id = ?",
                    fleet.getFleetId());

            while (rs.next()) {
                Car car = createCarFromResultSet(rs);
                fleet.addCar(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Car createCarFromResultSet(ResultSet rs) throws SQLException {
        String make = rs.getString("make");
        String model = rs.getString("model");
        double price = rs.getDouble("price");
        double maxSpeed = rs.getDouble("max_speed");
        String fuelType = rs.getString("fuel_type");
        double consumption = rs.getDouble("fuel_consumption");
        int carId = rs.getInt("car_id");

        Car car;
        if (fuelType.equalsIgnoreCase("Електричний")) {
            car = new ElectricCar(make, model, price, maxSpeed, consumption);
        } else {
            car = new GasCar(make, model, price, maxSpeed, consumption, fuelType);
        }
        car.setCarId(carId);
        return car;
    }

    private void saveFleetToDatabase(TaxiFleet fleet) {
        try {
            if (fleet.getFleetId() == 0) {
                dbManager.executeUpdate(
                        "INSERT INTO fleets (name) VALUES (?)",
                        fleet.getName());

                ResultSet rs = dbManager.executeQuery("SELECT LAST_INSERT_ID()");
                if (rs.next()) {
                    fleet.setFleetId(rs.getInt(1));
                }
            } else {
                dbManager.executeUpdate(
                        "UPDATE fleets SET name = ? WHERE fleet_id = ?",
                        fleet.getName(), fleet.getFleetId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}