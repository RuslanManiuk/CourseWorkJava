package database;

import models.cars.Car;
import models.cars.ElectricCar;
import models.cars.GasCar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataBaseManagerTest {
    private DataBaseManager dbManager;

    @BeforeEach
    void setUp() {
        try {
            Class.forName("org.h2.Driver");
            System.out.println("H2 driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("H2 driver not found");
            throw new RuntimeException(e);
        }
        dbManager = new DataBaseManager(true);
    }

    @AfterEach
    void tearDown() {
        dbManager.close();
    }

    @Test
    void testInitializeConnection() {
        assertNotNull(dbManager, "DataBaseManager should be initialized");
        assertDoesNotThrow(() -> dbManager.executeQuery("SELECT 1"), "Connection should be valid");
    }

    @Test
    void testExecuteUpdateAndQuery() throws SQLException {
        // Створення тестового автопарку
        dbManager.executeUpdate("INSERT INTO fleets (name) VALUES (?)", "Test Fleet");

        // Перевірка створення
        ResultSet rs = dbManager.executeQuery("SELECT * FROM fleets WHERE name = ?", "Test Fleet");
        assertTrue(rs.next(), "Fleet should be created");
        assertEquals("Test Fleet", rs.getString("name"), "Fleet name should match");
    }

    @Test
    void testSaveAndLoadCar() throws SQLException {
        // Створення тестового автопарку
        dbManager.executeUpdate("INSERT INTO fleets (name) VALUES (?)", "Test Fleet");
        ResultSet rs = dbManager.executeQuery("SELECT fleet_id FROM fleets WHERE name = ?", "Test Fleet");
        assertTrue(rs.next());
        int fleetId = rs.getInt("fleet_id");

        // Тестування збереження та завантаження електричного автомобіля
        Car electricCar = new ElectricCar("Tesla", "Model S", 80000, 250, 15.5);
        dbManager.saveCar(electricCar, fleetId);

        List<Car> cars = dbManager.loadCarsForFleet(fleetId);
        assertEquals(1, cars.size(), "Should load 1 car");
        Car loadedCar = cars.get(0);

        assertTrue(loadedCar instanceof ElectricCar, "Loaded car should be ElectricCar");
        assertEquals("Tesla", loadedCar.getMake());
        assertEquals("Model S", loadedCar.getModel());
        assertEquals(80000, loadedCar.getPrice());
        assertEquals(250, loadedCar.getMaxSpeed());
        assertEquals(15.5, loadedCar.getFuelConsumption());

        // Тестування збереження та завантаження бензинового автомобіля
        Car gasCar = new GasCar("Toyota", "Camry", 30000, 200, 8.5, "Бензин");
        dbManager.saveCar(gasCar, fleetId);

        cars = dbManager.loadCarsForFleet(fleetId);
        assertEquals(2, cars.size(), "Should load 2 cars");

        boolean foundGasCar = cars.stream()
                .filter(c -> c instanceof GasCar)
                .anyMatch(c -> c.getMake().equals("Toyota"));
        assertTrue(foundGasCar, "Gas car should be loaded");
    }

    @Test
    void testCreateCarFromResultSet() throws SQLException {
        // Створення тестового автопарку
        dbManager.executeUpdate("INSERT INTO fleets (name) VALUES (?)", "Test Fleet");
        ResultSet rs = dbManager.executeQuery("SELECT fleet_id FROM fleets WHERE name = ?", "Test Fleet");
        assertTrue(rs.next());
        int fleetId = rs.getInt("fleet_id");

        // Додаємо тестові дані безпосередньо в базу
        dbManager.executeUpdate(
                "INSERT INTO cars (fleet_id, make, model, price, max_speed, fuel_type, fuel_consumption) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                fleetId, "Tesla", "Model 3", 50000, 220, "Електричний", 14.0);

        dbManager.executeUpdate(
                "INSERT INTO cars (fleet_id, make, model, price, max_speed, fuel_type, fuel_consumption) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                fleetId, "Toyota", "RAV4", 35000, 180, "Бензин", 7.5);

        // Отримуємо дані для перевірки
        rs = dbManager.executeQuery("SELECT * FROM cars WHERE fleet_id = ?", fleetId);

        // Перевіряємо створення електричного автомобіля
        assertTrue(rs.next());
        Car electricCar = dbManager.createCarFromResultSet(rs);
        assertTrue(electricCar instanceof ElectricCar);
        assertEquals("Tesla", electricCar.getMake());
        assertEquals("Model 3", electricCar.getModel());

        // Перевіряємо створення бензинового автомобіля
        assertTrue(rs.next());
        Car gasCar = dbManager.createCarFromResultSet(rs);
        assertTrue(gasCar instanceof GasCar);
        assertEquals("Toyota", ((GasCar) gasCar).getMake());
        assertEquals("RAV4", ((GasCar) gasCar).getModel());
        assertEquals("Бензин", ((GasCar) gasCar).getFuelType());
    }

    @Test
    void testCloseConnection() {
        assertDoesNotThrow(() -> dbManager.close(), "Closing connection should not throw exception");

        // Спроба виконати запит після закриття з'єднання
        assertThrows(SQLException.class, () -> dbManager.executeQuery("SELECT 1"),
                "Should throw SQLException when connection is closed");
    }
}