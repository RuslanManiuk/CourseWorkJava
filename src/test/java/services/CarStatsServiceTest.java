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

class CarStatsServiceTest {

    @Mock
    private TaxiFleet taxiFleet;

    private CarStatsService carStatsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        carStatsService = new CarStatsService(taxiFleet);
    }

    @Test
    void testCalculateAverageElectricConsumption_WithElectricCars() {
        // Arrange
        Car electricCar1 = createMockCar("Електричний", 0.15);
        Car electricCar2 = createMockCar("Електричний", 0.18);
        when(taxiFleet.getCars()).thenReturn(Arrays.asList(electricCar1, electricCar2));

        // Act
        double result = carStatsService.calculateAverageElectricConsumption();

        // Assert
        assertEquals(0.165, result, 0.001);
    }

    @Test
    void testCalculateAverageElectricConsumption_NoElectricCars() {
        // Arrange
        Car gasCar = createMockCar("Бензин", 8.5);
        Car dieselCar = createMockCar("Дизель", 9.5);
        when(taxiFleet.getCars()).thenReturn(Arrays.asList(gasCar, dieselCar));

        // Act
        double result = carStatsService.calculateAverageElectricConsumption();

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void testCalculateAverageElectricConsumption_EmptyFleet() {
        // Arrange
        when(taxiFleet.getCars()).thenReturn(Collections.emptyList());

        // Act
        double result = carStatsService.calculateAverageElectricConsumption();

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void testCalculateAverageGasConsumption_WithGasCars() {
        // Arrange
        Car gasCar1 = createMockCar("Бензин", 8.5);
        Car gasCar2 = createMockCar("Дизель", 9.5);
        Car hybridCar = createMockCar("Гібрид", 4.5);
        when(taxiFleet.getCars()).thenReturn(Arrays.asList(gasCar1, gasCar2, hybridCar));

        // Act
        double result = carStatsService.calculateAverageGasConsumption();

        // Assert
        assertEquals(7.5, result, 0.001); // (8.5 + 9.5 + 4.5) / 3 = 7.5
    }

    @Test
    void testCalculateAverageGasConsumption_OnlyElectricCars() {
        // Arrange
        Car electricCar1 = createMockCar("Електричний", 0.15);
        Car electricCar2 = createMockCar("Електричний", 0.18);
        when(taxiFleet.getCars()).thenReturn(Arrays.asList(electricCar1, electricCar2));

        // Act
        double result = carStatsService.calculateAverageGasConsumption();

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void testCalculateAverageGasConsumption_EmptyFleet() {
        // Arrange
        when(taxiFleet.getCars()).thenReturn(Collections.emptyList());

        // Act
        double result = carStatsService.calculateAverageGasConsumption();

        // Assert
        assertEquals(0.0, result, 0.001);
    }

    private Car createMockCar(String fuelType, double fuelConsumption) {
        Car car = mock(Car.class);
        when(car.getFuelType()).thenReturn(fuelType);
        when(car.getFuelConsumption()).thenReturn(fuelConsumption);
        return car;
    }
}