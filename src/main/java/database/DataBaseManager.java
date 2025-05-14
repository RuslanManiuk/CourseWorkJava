package database;

import models.cars.Car;
import models.cars.ElectricCar;
import models.cars.GasCar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DataBaseManager {
    private static final Logger logger = LogManager.getLogger(DataBaseManager.class);
    private static final String URL = "jdbc:mysql://localhost:3307/taxi_fleet_db";
    private static final String USER = "root";
    private static final String PASSWORD = "77713";

    private Connection connection;

    public DataBaseManager() {
        try {
            logger.info("Initializing database connection");
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.info("Database connection established successfully");
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Failed to initialize database connection", e);
            throw new RuntimeException("Помилка підключення до бази даних");
        }
    }

    public void executeUpdate(String sql, Object... params) throws SQLException {
        logger.debug("Executing SQL update: {}", sql);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            statement.executeUpdate();
            logger.debug("SQL update executed successfully");
        } catch (SQLException e) {
            logger.error("Failed to execute SQL update: {}", sql, e);
            throw e;
        }
    }

    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        logger.debug("Executing SQL query: {}", sql);
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        return statement.executeQuery();
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed successfully");
            }
        } catch (SQLException e) {
            logger.error("Failed to close database connection", e);
        }
    }

    public void saveCar(Car car, int fleetId) throws SQLException {
        logger.info("Saving car to database: {} {}, fleetId: {}", car.getMake(), car.getModel(), fleetId);
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
        logger.info("Car saved successfully");
    }

    public List<Car> loadCarsForFleet(int fleetId) throws SQLException {
        logger.info("Loading cars for fleetId: {}", fleetId);
        List<Car> cars = new ArrayList<>();
        ResultSet rs = executeQuery(
                "SELECT * FROM cars WHERE fleet_id = ?",
                fleetId);

        while (rs.next()) {
            Car car = createCarFromResultSet(rs);
            cars.add(car);
        }
        logger.info("Loaded {} cars for fleetId: {}", cars.size(), fleetId);
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