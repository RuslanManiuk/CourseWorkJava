package models;

import database.DataBaseManager;
import models.cars.Car;
import models.cars.ElectricCar;
import models.cars.GasCar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaxiFleetManagerTest {

    @Mock
    private DataBaseManager dbManager;

    @Mock
    private ResultSet fleetResultSet;

    @Mock
    private ResultSet carResultSet;

    @Mock
    private ResultSet lastInsertIdResultSet;

    private TaxiFleetManager taxiFleetManager;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        taxiFleetManager = new TaxiFleetManager();
        taxiFleetManager.dbManager = dbManager;

        // Налаштування мока для LAST_INSERT_ID
        when(lastInsertIdResultSet.next()).thenReturn(true);
        when(lastInsertIdResultSet.getInt(1)).thenReturn(1);
        when(dbManager.executeQuery("SELECT LAST_INSERT_ID()")).thenReturn(lastInsertIdResultSet);
    }

    @Test
    void testConstructorInitializesFleets() {
        TaxiFleetManager manager = new TaxiFleetManager(true); // У тестовому режимі
        assertNotNull(manager.getFleets(), "Fleets list should not be null");
        assertTrue(manager.getFleets().isEmpty(), "Fleets list should be empty initially");
    }

    @Test
    void testAddFleet() throws SQLException {
        TaxiFleet fleet = new TaxiFleet("New Fleet");

        taxiFleetManager.addFleet(fleet);

        assertTrue(taxiFleetManager.getFleets().contains(fleet));
        verify(dbManager).executeUpdate("INSERT INTO fleets (name) VALUES (?)", "New Fleet");
        verify(dbManager).executeQuery("SELECT LAST_INSERT_ID()");
        assertEquals(1, fleet.getFleetId());
    }

    @Test
    void testRemoveFleet() throws SQLException {
        TaxiFleet fleet = new TaxiFleet("Test Fleet");
        fleet.setFleetId(1);
        taxiFleetManager.getFleets().add(fleet);

        taxiFleetManager.removeFleet(fleet);

        assertFalse(taxiFleetManager.getFleets().contains(fleet));
        verify(dbManager).executeUpdate("DELETE FROM fleets WHERE fleet_id = ?", 1);
    }

    @Test
    void testLoadFleetsFromDatabase() throws SQLException {
        // 1. Очистимо існуючі дані
        taxiFleetManager.getFleets().clear();

        // 2. Налаштуємо моки для одного флоту
        when(dbManager.executeQuery("SELECT * FROM fleets")).thenReturn(fleetResultSet);
        when(fleetResultSet.next()).thenReturn(true, false); // Один рядок
        when(fleetResultSet.getInt("fleet_id")).thenReturn(1);
        when(fleetResultSet.getString("name")).thenReturn("Test Fleet");

        // 3. Налаштуємо моки для автомобілів (1 автомобіль)
        when(dbManager.executeQuery("SELECT * FROM cars WHERE fleet_id = ?", 1))
                .thenReturn(carResultSet);
        when(carResultSet.next()).thenReturn(true, false);
        when(carResultSet.next()).thenReturn(true, false); // Один автомобіль
        when(carResultSet.getString("make")).thenReturn("Tesla");
        when(carResultSet.getString("model")).thenReturn("Model S");
        when(carResultSet.getDouble("price")).thenReturn(80000.0);
        when(carResultSet.getDouble("max_speed")).thenReturn(250.0);
        when(carResultSet.getString("fuel_type")).thenReturn("Електричний");
        when(carResultSet.getDouble("fuel_consumption")).thenReturn(0.0);
        when(carResultSet.getInt("car_id")).thenReturn(1);

        // 4. Викликаємо метод
        taxiFleetManager.loadFleetsFromDatabase();

        // 5. Перевірки
        assertAll(
                () -> assertEquals(1, taxiFleetManager.getFleets().size(),
                        "Має бути завантажено рівно 1 флот"),
                () -> assertEquals("Test Fleet", taxiFleetManager.getFleets().get(0).getName()),
                () -> assertEquals(1, taxiFleetManager.getFleets().get(0).getFleetId())
        );
    }

    @Test
    void testLoadFleetsFromDatabase_EmptyDatabase() throws SQLException {
        // 1. Підготовка
        taxiFleetManager.getFleets().clear(); // Явне очищення

        // 2. Налаштування моків
        when(dbManager.executeQuery("SELECT * FROM fleets")).thenReturn(fleetResultSet);
        when(fleetResultSet.next()).thenReturn(false); // Імітація пустої БД

        // 3. Виконання
        taxiFleetManager.loadFleetsFromDatabase();

        // 4. Перевірки
        assertAll(
                () -> assertEquals(0, taxiFleetManager.getFleets().size(),
                        "Список флотів має бути порожнім"),
                () -> verify(dbManager).executeQuery("SELECT * FROM fleets"),
                () -> verify(fleetResultSet).next()
        );
    }

    @Test
    void testLoadFleetsFromDatabase_SQLException() throws SQLException {
        // 1. Переконаємось, що список спочатку порожній
        taxiFleetManager.getFleets().clear();

        // 2. Мокуємо виняток
        when(dbManager.executeQuery("SELECT * FROM fleets")).thenThrow(new SQLException("DB error"));

        // 3. Викликаємо метод і перевіряємо, що виняток не прокидається
        assertDoesNotThrow(() -> taxiFleetManager.loadFleetsFromDatabase());

        // 4. Перевіряємо, що список залишився порожнім
        assertTrue(taxiFleetManager.getFleets().isEmpty(), "Fleets list should be empty after SQLException");
    }

    @Test
    void testSaveFleetToDatabase_NewFleet() throws SQLException {
        TaxiFleet fleet = new TaxiFleet("New Fleet");

        taxiFleetManager.saveFleetToDatabase(fleet);

        verify(dbManager).executeUpdate("INSERT INTO fleets (name) VALUES (?)", "New Fleet");
        verify(dbManager).executeQuery("SELECT LAST_INSERT_ID()");
        assertEquals(1, fleet.getFleetId());
    }

    @Test
    void testSaveFleetToDatabase_ExistingFleet() throws SQLException {
        TaxiFleet fleet = new TaxiFleet("Existing Fleet");
        fleet.setFleetId(1);

        taxiFleetManager.saveFleetToDatabase(fleet);

        verify(dbManager).executeUpdate("UPDATE fleets SET name = ? WHERE fleet_id = ?", "Existing Fleet", 1);
        verify(dbManager, never()).executeQuery("SELECT LAST_INSERT_ID()");
    }

    @Test
    void testCreateCarFromResultSet() throws SQLException {
        when(carResultSet.getString("make")).thenReturn("Tesla");
        when(carResultSet.getString("model")).thenReturn("Model 3");
        when(carResultSet.getDouble("price")).thenReturn(45000.0);
        when(carResultSet.getDouble("max_speed")).thenReturn(230.0);
        when(carResultSet.getString("fuel_type")).thenReturn("Електричний");
        when(carResultSet.getDouble("fuel_consumption")).thenReturn(0.0);
        when(carResultSet.getInt("car_id")).thenReturn(10);

        Car car = taxiFleetManager.createCarFromResultSet(carResultSet);

        assertTrue(car instanceof ElectricCar);
        assertEquals("Tesla", car.getMake());
        assertEquals("Model 3", car.getModel());
        assertEquals(45000.0, car.getPrice());
        assertEquals(230.0, car.getMaxSpeed());
        assertEquals(10, car.getCarId());
    }
}