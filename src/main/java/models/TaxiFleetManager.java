package models;

import database.DataBaseManager;
import models.cars.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Клас, що управляє колекцією таксопарків.
 * Забезпечує взаємодію з базою даних для збереження та завантаження інформації
 * про таксопарки та автомобілі.
 */
public class TaxiFleetManager {
    private static final Logger logger = LogManager.getLogger(TaxiFleetManager.class);
    private List<TaxiFleet> fleets = new ArrayList<>();
    private DataBaseManager dbManager;

    /**
     * Конструктор для створення нового менеджера таксопарків.
     * Автоматично завантажує дані про таксопарки з бази даних при створенні.
     */
    public TaxiFleetManager() {
        this.dbManager = new DataBaseManager();
        loadFleetsFromDatabase(); // Завантажуємо дані при створенні
    }

    // ------------------ Основні методи доступу ------------------

    /**
     * Отримує список всіх таксопарків.
     *
     * @return список об'єктів TaxiFleet
     */
    public List<TaxiFleet> getFleets() {
        return fleets;
    }

    /**
     * Отримує таксопарк за індексом у списку.
     *
     * @param index індекс таксопарку
     * @return об'єкт TaxiFleet
     * @throws IndexOutOfBoundsException якщо індекс недійсний
     */
    public TaxiFleet getFleet(int index) {
        if (index >= 0 && index < fleets.size()) {
            return fleets.get(index);
        }
        throw new IndexOutOfBoundsException("Invalid fleet index: " + index);
    }

    // ------------------ Управління таксопарками ------------------

    /**
     * Додає існуючий таксопарк до менеджера та зберігає його в базі даних.
     *
     * @param fleet таксопарк для додавання
     */
    public void addFleet(TaxiFleet fleet) {
        logger.info("Adding new fleet: {}", fleet.getName());
        fleets.add(fleet);
        saveFleetToDatabase(fleet);
    }

    /**
     * Створює новий таксопарк з вказаною назвою, додає його до менеджера
     * та зберігає в базі даних.
     *
     * @param name назва нового таксопарку
     */
    public void createFleet(String name) {
        TaxiFleet fleet = new TaxiFleet(name);
        fleets.add(fleet);
        saveFleetToDatabase(fleet);
    }

    /**
     * Видаляє таксопарк з менеджера та з бази даних.
     *
     * @param fleet таксопарк для видалення
     */
    public void removeFleet(TaxiFleet fleet) {
        logger.info("Removing fleet: {}", fleet.getName());
        fleets.remove(fleet);
        try {
            dbManager.executeUpdate("DELETE FROM fleets WHERE fleet_id = ?", fleet.getFleetId());
        } catch (SQLException e) {
            logger.error("Error removing fleet from database", e);
        }
    }

    // ------------------ Операції з базою даних ------------------

    /**
     * Завантажує всі таксопарки з бази даних.
     * Викликається автоматично при створенні менеджера.
     */
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

    /**
     * Завантажує автомобілі для вказаного таксопарку з бази даних.
     *
     * @param fleet таксопарк, для якого завантажуються автомобілі
     */
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

    /**
     * Зберігає таксопарк у базі даних.
     * Якщо таксопарк новий (має ID = 0), створює новий запис.
     * Інакше оновлює існуючий запис.
     *
     * @param fleet таксопарк для збереження
     */
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

    /**
     * Створює об'єкт автомобіля (ElectricCar або GasCar) з результату запиту до бази даних.
     *
     * @param rs ResultSet з даними про автомобіль
     * @return створений об'єкт Car
     * @throws SQLException при проблемах з доступом до даних
     */
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
}