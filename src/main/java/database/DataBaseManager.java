package database;

import models.cars.Car;
import models.cars.ElectricCar;
import models.cars.GasCar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DataBaseManager {
    private static final String URL = "jdbc:mysql://localhost:3307/TaxiFleetLocalDB";
    private static final String USER = "root";
    private static final String PASSWORD = "77713";

    private Connection connection;

    public DataBaseManager() {
        try {
            // Реєструємо драйвер
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Встановлюємо з'єднання
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Помилка підключення до бази даних");
        }
    }

    // Метод для виконання запитів без результату (INSERT, UPDATE, DELETE)
    public void executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            statement.executeUpdate();
        }
    }

    // Метод для виконання запитів з результатом (SELECT)
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        return statement.executeQuery();
    }

    // Метод для закриття з'єднання
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Додаткові методи для конкретних операцій з вашою БД

    public void saveCar(Car car, int fleetId) throws SQLException {
        String sql = "INSERT INTO cars (fleet_id, make, model, price, max_speed, fuel_type, fuel_consumption) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        executeUpdate(sql,
                fleetId,
                car.getMake(),
                car.getModel(),
                car.getPrice(),
                car.getMaxSpeed(),
                car.getFuelType(),
                car.getFuelConsumption());
    }

    public List<Car> loadCarsForFleet(int fleetId) throws SQLException {
        List<Car> cars = new ArrayList<>();
        ResultSet rs = executeQuery(
                "SELECT * FROM cars WHERE fleet_id = ?",
                fleetId);

        while (rs.next()) {
            Car car = createCarFromResultSet(rs);
            cars.add(car);
        }
        return cars;
    }

    private Car createCarFromResultSet(ResultSet rs) throws SQLException {
        String fuelType = rs.getString("fuel_type");
        if ("Електричний".equals(fuelType)) {
            return new ElectricCar(
                    rs.getString("make"),
                    rs.getString("model"),
                    rs.getDouble("price"),
                    rs.getDouble("max_speed"),
                    rs.getDouble("fuel_consumption"));
        } else {
            return new GasCar(
                    rs.getString("make"),
                    rs.getString("model"),
                    rs.getDouble("price"),
                    rs.getDouble("max_speed"),
                    rs.getDouble("fuel_consumption"),
                    fuelType);
        }
    }
}