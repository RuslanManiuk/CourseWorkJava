package models;

import database.DataBaseManager;
import models.cars.Car;

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

    public TaxiFleet getFleet(int index) {
        if (index >= 0 && index < fleets.size()) {
            return fleets.get(index);
        }
        throw new IndexOutOfBoundsException("Invalid fleet index: " + index);
    }

    public List<TaxiFleet> getAllFleets() {
        return fleets;
    }

    public void createFleet(String name) {
        TaxiFleet fleet = new TaxiFleet(name);
        fleets.add(fleet);
        saveFleetToDatabase(fleet);
    }

    private void loadFleetsFromDatabase() {
        try {
            ResultSet rs = dbManager.executeQuery("SELECT * FROM fleets");
            while (rs.next()) {
                TaxiFleet fleet = new TaxiFleet(rs.getString("name"));
                fleet.setFleetId(rs.getInt("fleet_id")); // Додайте setId в TaxiFleet
                loadCarsForFleet(fleet); // Завантажуємо авто для кожного таксопарку
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
        // Реалізація створення авто з ResultSet
        return null;
    }

    private void saveFleetToDatabase(TaxiFleet fleet) {
        try {
            if (fleet.getFleetId() == 0) { // Новий таксопарк
                dbManager.executeUpdate(
                        "INSERT INTO fleets (name) VALUES (?)",
                        fleet.getName());

                // Отримуємо ID нового таксопарку
                ResultSet rs = dbManager.executeQuery("SELECT LAST_INSERT_ID()");
                if (rs.next()) {
                    fleet.setFleetId(rs.getInt(1));
                }
            } else { // Оновлення що існує
                dbManager.executeUpdate(
                        "UPDATE fleets SET name = ? WHERE fleet_id = ?",
                        fleet.getName(), fleet.getFleetId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Інші методи залишаються без змін...
}