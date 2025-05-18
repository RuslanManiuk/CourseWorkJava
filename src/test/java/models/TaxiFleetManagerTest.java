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
        assertNotNull(taxiFleetManager.getFleets());
        assertTrue(taxiFleetManager.getFleets().isEmpty());
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
        // Налаштування моків для завантаження флоту
        when(dbManager.executeQuery("SELECT * FROM fleets")).thenReturn(fleetResultSet);
        when(fleetResultSet.next()).thenReturn(true, false); // Один флот
        when(fleetResultSet.getInt("fleet_id")).thenReturn(1);
        when(fleetResultSet.getString("name")).thenReturn("Test Fleet");

        // Налаштування моків для завантаження автомобілів
        when(dbManager.executeQuery("SELECT * FROM cars WHERE fleet_id = ?", 1)).thenReturn(carResultSet);
        when(carResultSet.next()).thenReturn(true, false); // Один автомобіль
        when(carResultSet.getString("make")).thenReturn("Tesla");
        when(carResultSet.getString("model")).thenReturn("Model S");
        when(carResultSet.getDouble("price")).thenReturn(80000.0);
        when(carResultSet.getDouble("max_speed")).thenReturn(250.0);
        when(carResultSet.getString("fuel_type")).thenReturn("Електричний");
        when(carResultSet.getDouble("fuel_consumption")).thenReturn(0.0);
        when(carResultSet.getInt("car_id")).thenReturn(1);

        taxiFleetManager.loadFleetsFromDatabase();

        assertEquals(1, taxiFleetManager.getFleets().size());
        TaxiFleet loadedFleet = taxiFleetManager.getFleets().get(0);
        assertEquals("Test Fleet", loadedFleet.getName());
        assertEquals(1, loadedFleet.getFleetId());
        assertEquals(1, loadedFleet.getCars().size());

        // Перевірка порядку викликів
        InOrder inOrder = inOrder(dbManager, fleetResultSet, carResultSet);
        inOrder.verify(dbManager).executeQuery("SELECT * FROM fleets");
        inOrder.verify(fleetResultSet).next();
        inOrder.verify(dbManager).executeQuery("SELECT * FROM cars WHERE fleet_id = ?", 1);
        inOrder.verify(carResultSet).next();
    }

    @Test
    void testLoadFleetsFromDatabase_EmptyDatabase() throws SQLException {
        when(dbManager.executeQuery("SELECT * FROM fleets")).thenReturn(fleetResultSet);
        when(fleetResultSet.next()).thenReturn(false); // Немає флотів

        taxiFleetManager.loadFleetsFromDatabase();

        assertTrue(taxiFleetManager.getFleets().isEmpty());
    }

    @Test
    void testLoadFleetsFromDatabase_SQLException() throws SQLException {
        when(dbManager.executeQuery("SELECT * FROM fleets")).thenThrow(new SQLException("DB error"));

        assertDoesNotThrow(() -> taxiFleetManager.loadFleetsFromDatabase());
        assertTrue(taxiFleetManager.getFleets().isEmpty());
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