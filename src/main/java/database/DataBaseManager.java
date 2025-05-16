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

/**
 * Менеджер бази даних для роботи з базою даних таксопарку.
 */
public class DataBaseManager {
    // Конфігурація логера
    private static final Logger logger = LogManager.getLogger(DataBaseManager.class);

    // Параметри підключення до бази даних
    private static final String URL = "jdbc:mysql://localhost:3307/taxi_fleet_db";
    private static final String USER = "root";
    private static final String PASSWORD = "77713";

    // З'єднання з базою даних
    private Connection connection;

    /**
     * Ініціалізує підключення до бази даних.
     * Викидає RuntimeException у разі невдалого підключення.
     */
    public DataBaseManager() {
        try {
            logger.info("Ініціалізація підключення до бази даних");
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.info("Підключення до бази даних успішно встановлено");
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Не вдалося ініціалізувати підключення до бази даних", e);
            throw new RuntimeException("Помилка підключення до бази даних");
        }
    }

    /**
     * Виконує SQL запит оновлення з параметрами.
     *
     * @param sql    SQL запит для виконання
     * @param params Параметри для підготовленого запиту
     * @throws SQLException якщо виконання не вдалося
     */
    public void executeUpdate(String sql, Object... params) throws SQLException {
        logger.debug("Виконання SQL оновлення: {}", sql);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            statement.executeUpdate();
            logger.debug("SQL оновлення виконано успішно");
        } catch (SQLException e) {
            logger.error("Не вдалося виконати SQL оновлення: {}", sql, e);
            throw e;
        }
    }

    /**
     * Виконує SQL запит з параметрами.
     *
     * @param sql    SQL запит для виконання
     * @param params Параметри для підготовленого запиту
     * @return ResultSet результат запиту
     * @throws SQLException якщо виконання не вдалося
     */
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        logger.debug("Виконання SQL запиту: {}", sql);
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        return statement.executeQuery();
    }

    /**
     * Закриває підключення до бази даних.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Підключення до бази даних успішно закрито");
            }
        } catch (SQLException e) {
            logger.error("Не вдалося закрити підключення до бази даних", e);
        }
    }

    /**
     * Зберігає автомобіль до бази даних.
     *
     * @param car     Автомобіль для збереження
     * @param fleetId Ідентифікатор автопарку
     * @throws SQLException якщо збереження не вдалося
     */
    public void saveCar(Car car, int fleetId) throws SQLException {
        logger.info("Збереження автомобіля до бази даних: {} {}, fleetId: {}", car.getMake(), car.getModel(), fleetId);
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
        logger.info("Автомобіль успішно збережено");
    }

    /**
     * Завантажує автомобілі для вказаного автопарку.
     *
     * @param fleetId Ідентифікатор автопарку
     * @return Список автомобілів
     * @throws SQLException якщо завантаження не вдалося
     */
    public List<Car> loadCarsForFleet(int fleetId) throws SQLException {
        logger.info("Завантаження автомобілів для fleetId: {}", fleetId);
        List<Car> cars = new ArrayList<>();
        ResultSet rs = executeQuery(
                "SELECT * FROM cars WHERE fleet_id = ?",
                fleetId);

        while (rs.next()) {
            Car car = createCarFromResultSet(rs);
            cars.add(car);
        }
        logger.info("Завантажено {} автомобілів для fleetId: {}", cars.size(), fleetId);
        return cars;
    }

    /**
     * Створює об'єкт автомобіля з результату запиту.
     *
     * @param rs Результат запиту
     * @return Об'єкт автомобіля
     * @throws SQLException якщо отримання даних не вдалося
     */
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