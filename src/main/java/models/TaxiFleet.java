package models;

import database.DataBaseManager;
import models.cars.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TaxiFleet {
    private List<Car> cars;
    private String name;
    private int fleetId;
    private DataBaseManager databaseManager = new DataBaseManager();

    public TaxiFleet(String name) {
        this.name = name;
        this.cars = new ArrayList<>();
    }

    public String getFleetName() {
        return name;
    }

    // Додаємо метод для зміни назви таксопарку
    public void setName(String name) {
        this.name = name;
        try {
            databaseManager.executeUpdate(
                    "UPDATE fleets SET name = ? WHERE fleet_id = ?",
                    name, this.fleetId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCar(Car car) {
        cars.add(car);
        saveCarToDatabase(car);
    }

    private void saveCarToDatabase(Car car) {
        DataBaseManager dbManager = new DataBaseManager();
        try {
            if (car.getCarId() == 0) { // Новий автомобіль
                dbManager.saveCar(car, this.fleetId);

                // Оновлюємо ID авто після збереження
                ResultSet rs = dbManager.executeQuery(
                        "SELECT car_id FROM cars WHERE make = ? AND model = ? AND fleet_id = ?",
                        car.getMake(), car.getModel(), this.fleetId);

                if (rs.next()) {
                    car.setCarId(rs.getInt("car_id"));
                }
            } else { // Оновлення існуючого авто
                dbManager.executeUpdate(
                        "UPDATE cars SET make = ?, model = ?, price = ?, max_speed = ?, " +
                                "fuel_type = ?, fuel_consumption = ? WHERE car_id = ?",
                        car.getMake(), car.getModel(), car.getPrice(), car.getMaxSpeed(),
                        car.getFuelType(), car.getFuelConsumption(), car.getCarId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbManager.close();
        }
    }

    public boolean removeCar(Car car) {
        boolean removed = cars.remove(car);
        if (removed) {
            DataBaseManager dbManager = new DataBaseManager();
            try {
                dbManager.executeUpdate(
                        "DELETE FROM cars WHERE car_id = ?",
                        car.getCarId());
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                dbManager.close();
            }
        }
        return removed;
    }

    public List<Car> getCars() { return cars; }
    public String getName() { return name; }
    public int getFleetId() { return fleetId; }
    public void setFleetId(int fleetId) { this.fleetId = fleetId; }

    // Новий функціонал
    public double calculateTotalCost() {
        return cars.stream().mapToDouble(Car::getPrice).sum();
    }

    public void sortByFuelConsumption() {
        Collections.sort(cars, Comparator.comparingDouble(Car::getFuelConsumption));
    }

    public List<Car> findCarsBySpeedRange(double minSpeed, double maxSpeed) {
        List<Car> result = new ArrayList<>();
        for (Car car : cars) {
            if (car.getMaxSpeed() >= minSpeed && car.getMaxSpeed() <= maxSpeed) {
                result.add(car);
            }
        }
        return result;
    }

    public double calculateAverageFuelConsumption() {
        if (cars.isEmpty()) return 0;
        return cars.stream()
                .mapToDouble(Car::getFuelConsumption)
                .average()
                .orElse(0);
    }

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

    public void printFuelStatistics() {
        long electric = cars.stream().filter(c -> c.getFuelType().equals("Електричний")).count();
        long gas = cars.stream().filter(c -> !c.getFuelType().equals("Електричний")).count();

        System.out.println("\nСтатистика таксопарку '" + name + "':");
        System.out.println("Електричні автомобілі: " + electric);
        System.out.println("Авто з ДВЗ: " + gas);
    }

    public int getElectricCarCount() {
        return (int) cars.stream().filter(c -> c.getFuelType().equals("Електричний")).count();
    }

    public int getGasCarCount() {
        return cars.size() - getElectricCarCount();
    }

    public List<Car> getSortedByFuelConsumption() {
        List<Car> sorted = new ArrayList<>(cars);
        sorted.sort(Comparator.comparingDouble(Car::getFuelConsumption));
        return sorted;
    }

    public void loadCarsFromDatabase() {
        try {
            this.cars = databaseManager.loadCarsForFleet(this.fleetId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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