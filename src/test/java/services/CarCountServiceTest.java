package services;

import models.TaxiFleet;
import models.cars.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CarCountServiceTest {

    @Mock
    private TaxiFleet taxiFleet;

    private CarCountService carCountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        carCountService = new CarCountService(taxiFleet);
    }

    @Test
    void testGetTotalCarCount() {
        // Arrange
        Car car1 = mock(Car.class);
        Car car2 = mock(Car.class);
        when(taxiFleet.getCars()).thenReturn(Arrays.asList(car1, car2));

        // Act
        int result = carCountService.getTotalCarCount();

        // Assert
        assertEquals(2, result);
    }

    @Test
    void testGetElectricCarCount() {
        // Arrange
        Car electricCar = createMockCar("Електричний");
        Car gasCar = createMockCar("Бензин");
        when(taxiFleet.getCars()).thenReturn(Arrays.asList(electricCar, gasCar, electricCar));

        // Act
        int result = carCountService.getElectricCarCount();

        // Assert
        assertEquals(2, result);
    }

    @Test
    void testGetGasCarCount() {
        // Arrange
        Car electricCar = createMockCar("Електричний");
        Car gasCar1 = createMockCar("Бензин");
        Car gasCar2 = createMockCar("Дизель");
        when(taxiFleet.getCars()).thenReturn(Arrays.asList(electricCar, gasCar1, gasCar2));

        // Act
        int result = carCountService.getGasCarCount();

        // Assert
        assertEquals(2, result);
    }

    @Test
    void testGetElectricCarCount_NoElectricCars() {
        // Arrange
        Car gasCar1 = createMockCar("Бензин");
        Car gasCar2 = createMockCar("Дизель");
        when(taxiFleet.getCars()).thenReturn(Arrays.asList(gasCar1, gasCar2));

        // Act
        int result = carCountService.getElectricCarCount();

        // Assert
        assertEquals(0, result);
    }

    @Test
    void testGetGasCarCount_NoGasCars() {
        // Arrange
        Car electricCar1 = createMockCar("Електричний");
        Car electricCar2 = createMockCar("Електричний");
        when(taxiFleet.getCars()).thenReturn(Arrays.asList(electricCar1, electricCar2));

        // Act
        int result = carCountService.getGasCarCount();

        // Assert
        assertEquals(0, result);
    }

    @Test
    void testGetTotalCarCount_EmptyFleet() {
        // Arrange
        when(taxiFleet.getCars()).thenReturn(Collections.emptyList());

        // Act
        int result = carCountService.getTotalCarCount();

        // Assert
        assertEquals(0, result);
    }

    private Car createMockCar(String fuelType) {
        Car car = mock(Car.class);
        when(car.getFuelType()).thenReturn(fuelType);
        return car;
    }
}