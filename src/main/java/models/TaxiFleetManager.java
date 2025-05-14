package models;

import database.DataBaseManager;
import models.cars.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaxiFleetManager {
    private static final Logger logger = LogManager.getLogger(TaxiFleetManager.class);
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
        logger.info("Adding new fleet: {}", fleet.getName());
        fleets.add(fleet);
        saveFleetToDatabase(fleet);
    }

    public void removeFleet(TaxiFleet fleet) {
        logger.info("Removing fleet: {}", fleet.getName());
        fleets.remove(fleet);
        try {
            dbManager.executeUpdate("DELETE FROM fleets WHERE fleet_id = ?", fleet.getFleetId());
        } catch (SQLException e) {
            logger.error("Error removing fleet from database", e);
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
        logger.info("Loading fleets from database");
        try {
            ResultSet rs = dbManager.executeQuery("SELECT * FROM fleets");
            while (rs.next()) {
                TaxiFleet fleet = new TaxiFleet(rs.getString("name"));
                fleet.setFleetId(rs.getInt("fleet_id"));
                loadCarsForFleet(fleet);
                fleets.add(fleet);
                logger.debug("Loaded fleet: {}", fleet.getName());
            }
            logger.info("Loaded {} fleets from database", fleets.size());
        } catch (SQLException e) {
            logger.error("Error loading fleets from database", e);
        }
    }

    private void loadCarsForFleet(TaxiFleet fleet) {
        logger.debug("Loading cars for fleet {}", fleet.getName());
        try {
            ResultSet rs = dbManager.executeQuery(
                    "SELECT * FROM cars WHERE fleet_id = ?",
                    fleet.getFleetId());

            while (rs.next()) {
                Car car = createCarFromResultSet(rs);
                fleet.addCar(car);
            }
        } catch (SQLException e) {
            logger.error("Error loading cars for fleet {}", fleet.getName(), e);
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
                logger.debug("Saving new fleet to database: {}", fleet.getName());
                dbManager.executeUpdate(
                        "INSERT INTO fleets (name) VALUES (?)",
                        fleet.getName());

                ResultSet rs = dbManager.executeQuery("SELECT LAST_INSERT_ID()");
                if (rs.next()) {
                    fleet.setFleetId(rs.getInt(1));
                    logger.info("New fleet saved with ID: {}", fleet.getFleetId());
                }
            } else {
                logger.debug("Updating existing fleet in database: {}", fleet.getName());
                dbManager.executeUpdate(
                        "UPDATE fleets SET name = ? WHERE fleet_id = ?",
                        fleet.getName(), fleet.getFleetId());
            }
        } catch (SQLException e) {
            logger.error("Error saving fleet to database", e);
        }
    }
}