package models;

import database.DataBaseManager;
import models.cars.Car;
import models.cars.ElectricCar;
import models.cars.GasCar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaxiFleetTest {
    private TaxiFleet taxiFleet;
    private DataBaseManager dbManager;
    private final String TEST_FLEET_NAME = "Test Fleet";
    private int testFleetId;

    @BeforeEach
    void setUp() throws SQLException {
        dbManager = new DataBaseManager();

        // Створюємо тестовий таксопарк в базі даних
        dbManager.executeUpdate("INSERT INTO fleets (name) VALUES (?)", TEST_FLEET_NAME);

        // Отримуємо ID щойно створеного таксопарку
        var rs = dbManager.executeQuery("SELECT fleet_id FROM fleets WHERE name = ?", TEST_FLEET_NAME);
        assertTrue(rs.next());
        testFleetId = rs.getInt("fleet_id");

        taxiFleet = new TaxiFleet(TEST_FLEET_NAME);
        taxiFleet.setFleetId(testFleetId);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Видаляємо всі тестові дані
        dbManager.executeUpdate("DELETE FROM cars WHERE fleet_id = ?", testFleetId);
        dbManager.executeUpdate("DELETE FROM fleets WHERE fleet_id = ?", testFleetId);
        dbManager.close();
    }

    @Test
    void testAddAndRemoveCar() throws SQLException {
        Car electricCar = new ElectricCar("Tesla", "Model 3", 45000, 250, 15);

        // Перевіряємо, що спочатку машин немає
        assertEquals(0, taxiFleet.getCars().size());

        // Додаємо машину
        taxiFleet.addCar(electricCar);
        assertEquals(1, taxiFleet.getCars().size());
        assertTrue(electricCar.getCarId() > 0); // ID має бути присвоєно

        // Перевіряємо, що машина дійсно збережена в БД
        var carsFromDb = dbManager.loadCarsForFleet(testFleetId);
        assertEquals(1, carsFromDb.size());
        assertEquals("Tesla", carsFromDb.get(0).getMake());

        // Видаляємо машину
        boolean removed = taxiFleet.removeCar(electricCar);
        assertTrue(removed);
        assertEquals(0, taxiFleet.getCars().size());

        // Перевіряємо, що машини немає в БД
        carsFromDb = dbManager.loadCarsForFleet(testFleetId);
        assertEquals(0, carsFromDb.size());
    }

    @Test
    void testLoadCarsFromDatabase() throws SQLException {
        // Додаємо тестові машини безпосередньо в БД
        dbManager.saveCar(new ElectricCar("Tesla", "Model S", 80000, 250, 18), testFleetId);
        dbManager.saveCar(new GasCar("Toyota", "Camry", 30000, 210, 8, "Бензин"), testFleetId);

        // Завантажуємо машини з БД
        taxiFleet.loadCarsFromDatabase();

        // Перевіряємо результати
        List<Car> cars = taxiFleet.getCars();
        assertEquals(2, cars.size());
        assertEquals("Tesla", cars.get(0).getMake());
        assertEquals("Toyota", cars.get(1).getMake());
    }

//    @Test
//    void testCalculateTotalCost() {
//        taxiFleet.addCar(new ElectricCar("Tesla", "Model 3", 45000, 250, 15));
//        taxiFleet.addCar(new GasCar("Toyota", "Corolla", 25000, 200, 7, "Бензин"));
//
//        double totalCost = taxiFleet.calculateTotalCost();
//        assertEquals(70000, totalCost, 0.001);
//    }
//
//    @Test
//    void testGetElectricAndGasCarCount() {
//        taxiFleet.addCar(new ElectricCar("Tesla", "Model 3", 45000, 250, 15));
//        taxiFleet.addCar(new ElectricCar("Nissan", "Leaf", 30000, 180, 12));
//        taxiFleet.addCar(new GasCar("Toyota", "Corolla", 25000, 200, 7, "Бензин"));
//
//        assertEquals(2, taxiFleet.getElectricCarCount());
//        assertEquals(1, taxiFleet.getGasCarCount());
//    }

    @Test
    void testSetName() throws SQLException {
        String newName = "New Fleet Name";
        taxiFleet.setName(newName);

        assertEquals(newName, taxiFleet.getName());

        // Перевіряємо, що ім'я оновилося в БД
        var rs = dbManager.executeQuery("SELECT name FROM fleets WHERE fleet_id = ?", testFleetId);
        assertTrue(rs.next());
        assertEquals(newName, rs.getString("name"));
    }

    @Test
    void testToString() {
        // Створюємо тестові автомобілі
        GasCar gasCar = new GasCar("Toyota", "Camry", 25000, 180, 8.5, "Бензин");
        ElectricCar electricCar = new ElectricCar("Tesla", "Model 3", 45000, 220, 15);

        // Додаємо їх до таксопарку
        taxiFleet.getCars().add(gasCar);
        taxiFleet.getCars().add(electricCar);

        // Очікуваний результат
        String expected = "Таксопарк 'Test Fleet' містить 2 авто:\n" +
                "- " + gasCar.toString() + "\n" +
                "- " + electricCar.toString() + "\n";

        // Порівнюємо з фактичним результатом
        assertEquals(expected, taxiFleet.toString());
    }
}