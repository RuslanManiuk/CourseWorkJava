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
    private DataBaseManager databaseManager = new DataBaseManager();

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
     * Альтернативний метод для отримання назви таксопарку.
     *
     * @return назва таксопарку
     */
    public String getFleetName() {
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

    // ------------------ Аналітичні методи ------------------

    /**
     * Обчислює загальну вартість усіх автомобілів у таксопарку.
     *
     * @return загальна вартість автопарку
     */
    public double calculateTotalCost() {
        return cars.stream().mapToDouble(Car::getPrice).sum();
    }

    /**
     * Обчислює середнє споживання палива для всіх автомобілів у таксопарку.
     *
     * @return середнє споживання палива
     */
    public double calculateAverageFuelConsumption() {
        if (cars.isEmpty()) return 0;
        return cars.stream()
                .mapToDouble(Car::getFuelConsumption)
                .average()
                .orElse(0);
    }

    /**
     * Повертає кількість електричних автомобілів у таксопарку.
     *
     * @return кількість електричних автомобілів
     */
    public int getElectricCarCount() {
        return (int) cars.stream().filter(c -> c.getFuelType().equals("Електричний")).count();
    }

    /**
     * Повертає кількість автомобілів з двигуном внутрішнього згоряння у таксопарку.
     *
     * @return кількість автомобілів з ДВЗ
     */
    public int getGasCarCount() {
        return cars.size() - getElectricCarCount();
    }

    /**
     * Виводить статистику щодо видів палива автомобілів у таксопарку.
     */
    public void printFuelStatistics() {
        long electric = cars.stream().filter(c -> c.getFuelType().equals("Електричний")).count();
        long gas = cars.stream().filter(c -> !c.getFuelType().equals("Електричний")).count();

        System.out.println("\nСтатистика таксопарку '" + name + "':");
        System.out.println("Електричні автомобілі: " + electric);
        System.out.println("Авто з ДВЗ: " + gas);
    }

    // ------------------ Методи сортування та пошуку ------------------

    /**
     * Сортує список автомобілів таксопарку за споживанням палива (у зростаючому порядку).
     */
    public void sortByFuelConsumption() {
        Collections.sort(cars, Comparator.comparingDouble(Car::getFuelConsumption));
    }

    /**
     * Повертає новий список автомобілів, відсортований за споживанням палива.
     *
     * @return відсортований список автомобілів
     */
    public List<Car> getSortedByFuelConsumption() {
        List<Car> sorted = new ArrayList<>(cars);
        sorted.sort(Comparator.comparingDouble(Car::getFuelConsumption));
        return sorted;
    }

    /**
     * Знаходить автомобілі, максимальна швидкість яких знаходиться у вказаному діапазоні.
     *
     * @param minSpeed мінімальна швидкість
     * @param maxSpeed максимальна швидкість
     * @return список автомобілів у заданому діапазоні швидкостей
     */
    public List<Car> findCarsBySpeedRange(double minSpeed, double maxSpeed) {
        List<Car> result = new ArrayList<>();
        for (Car car : cars) {
            if (car.getMaxSpeed() >= minSpeed && car.getMaxSpeed() <= maxSpeed) {
                result.add(car);
            }
        }
        return result;
    }

    /**
     * Сортує автомобілі за вказаним параметром у заданому порядку (зростання/спадання).
     *
     * @param parameter назва параметру для сортування (марка, модель, ціна, швидкість, витрата)
     * @param ascending true для сортування за зростанням, false для сортування за спаданням
     * @return відсортований список автомобілів
     */
    public List<Car> sortByParameter(String parameter, boolean ascending) {
        List<Car> sorted = new ArrayList<>(cars);

        Comparator<Car> comparator = switch (parameter.toLowerCase()) {
            case "марка" -> Comparator.comparing(Car::getMake);
            case "модель" -> Comparator.comparing(Car::getModel);
            case "ціна" -> Comparator.comparingDouble(Car::getPrice);
            case "швидкість" -> Comparator.comparingDouble(Car::getMaxSpeed);
            case "витрата" -> Comparator.comparingDouble(Car::getFuelConsumption);
            default -> (c1, c2) -> 0;
        };

        if (!ascending) {
            comparator = comparator.reversed();
        }

        sorted.sort(comparator);
        return sorted;
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