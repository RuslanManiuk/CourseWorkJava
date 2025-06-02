package models;

import database.DataBaseManager;
import models.cars.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Клас, що представляє таксопарк, який містить колекцію машин.
 * Забезпечує управління автомобілями, взаємодію з базою даних та аналіз автопарку.
 */
public class TaxiFleet {
    private static final Logger logger = LogManager.getLogger(TaxiFleet.class);
    private List<Car> cars;
    private String name;
    private int fleetId;
    DataBaseManager databaseManager = new DataBaseManager();

    /**
     * Конструктор для створення нового таксопарку з вказаною назвою.
     *
     * @param name назва таксопарку
     */
    public TaxiFleet(String name) {
        this.name = name;
        this.cars = new ArrayList<>();
    }

    // ------------------ Основні методи доступу ------------------

    /**
     * Повертає назву таксопарку.
     *
     * @return назва таксопарку
     */
    public String getName() {
        return name;
    }

    /**
     * Встановлює нову назву для таксопарку та оновлює інформацію в базі даних.
     *
     * @param name нова назва таксопарку
     */
    public void setName(String name) {
        logger.info("Renaming fleet from {} to {}", this.name, name);
        this.name = name;
        try {
            databaseManager.executeUpdate(
                    "UPDATE fleets SET name = ? WHERE fleet_id = ?",
                    name, this.fleetId);
        } catch (SQLException e) {
            logger.error("Error renaming fleet in database", e);
        }
    }

    /**
     * Повертає ідентифікатор таксопарку в базі даних.
     *
     * @return ідентифікатор таксопарку
     */
    public int getFleetId() {
        return fleetId;
    }

    /**
     * Встановлює ідентифікатор таксопарку.
     *
     * @param fleetId ідентифікатор таксопарку
     */
    public void setFleetId(int fleetId) {
        this.fleetId = fleetId;
    }

    /**
     * Повертає список усіх автомобілів у таксопарку.
     *
     * @return список автомобілів
     */
    public List<Car> getCars() {
        return cars;
    }

    // ------------------ Управління автомобілями ------------------

    /**
     * Додає новий автомобіль до таксопарку та зберігає його у базі даних.
     *
     * @param car автомобіль для додавання
     */
    public void addCar(Car car) {
        logger.debug("Adding car to fleet {}: {}", this.name, car);
        cars.add(car);
        saveCarToDatabase(car);
    }

    /**
     * Видаляє автомобіль з таксопарку та з бази даних.
     *
     * @param car автомобіль для видалення
     * @return true, якщо автомобіль був успішно видалений, інакше false
     */
    public boolean removeCar(Car car) {
        logger.debug("Removing car from fleet {}: {}", this.name, car);
        boolean removed = cars.remove(car);
        if (removed) {
            DataBaseManager dbManager = new DataBaseManager();
            try {
                dbManager.executeUpdate(
                        "DELETE FROM cars WHERE car_id = ?",
                        car.getCarId());
            } catch (SQLException e) {
                logger.error("Error removing car from database", e);
            } finally {
                dbManager.close();
            }
        }
        return removed;
    }

    /**
     * Завантажує автомобілі для цього таксопарку з бази даних.
     */
    public void loadCarsFromDatabase() {
        logger.info("Loading cars for fleet {} from database", this.name);
        try {
            this.cars = databaseManager.loadCarsForFleet(this.fleetId);
            logger.debug("Loaded {} cars for fleet {}", this.cars.size(), this.name);
        } catch (SQLException e) {
            logger.error("Error loading cars from database", e);
        }
    }

    /**
     * Зберігає інформацію про автомобіль у базі даних.
     *
     * @param car автомобіль для збереження
     */
    private void saveCarToDatabase(Car car) {
        DataBaseManager dbManager = new DataBaseManager();
        try {
            if (car.getCarId() == 0) {
                logger.debug("Saving new car to database: {}", car);
                dbManager.saveCar(car, this.fleetId);

                ResultSet rs = dbManager.executeQuery(
                        "SELECT car_id FROM cars WHERE make = ? AND model = ? AND fleet_id = ?",
                        car.getMake(), car.getModel(), this.fleetId);

                if (rs.next()) {
                    car.setCarId(rs.getInt("car_id"));
                }
            } else {
                logger.debug("Updating existing car in database: {}", car);
                dbManager.executeUpdate(
                        "UPDATE cars SET make = ?, model = ?, price = ?, max_speed = ?, " +
                                "fuel_type = ?, fuel_consumption = ? WHERE car_id = ?",
                        car.getMake(), car.getModel(), car.getPrice(), car.getMaxSpeed(),
                        car.getFuelType(), car.getFuelConsumption(), car.getCarId());
            }
        } catch (SQLException e) {
            logger.error("Error saving car to database", e);
        } finally {
            dbManager.close();
        }
    }

    // ------------------ Перевизначені методи ------------------

    /**
     * Повертає рядкове представлення таксопарку із перерахуванням усіх автомобілів.
     *
     * @return рядкове представлення таксопарку
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Таксопарк '").append(name).append("' містить ").append(cars.size()).append(" авто:\n");
        for (Car car : cars) {
            sb.append("- ").append(car).append("\n");
        }
        return sb.toString();
    }
}