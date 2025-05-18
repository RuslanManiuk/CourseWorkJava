package services;

import models.TaxiFleet;
import models.cars.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarFilterServiceTest {

    @Mock
    private TaxiFleet taxiFleet;

    private CarFilterService carFilterService;
    private List<Car> testCars;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        carFilterService = new CarFilterService(taxiFleet);
        testCars = createTestCars();
        when(taxiFleet.getCars()).thenReturn(testCars);
    }

    private List<Car> createTestCars() {
        List<Car> cars = new ArrayList<>();
        cars.add(createCar("Toyota", "Camry", "Бензин", 210, 25000, 8.5));
        cars.add(createCar("Toyota", "Corolla", "Бензин", 190, 22000, 7.5));
        cars.add(createCar("Tesla", "Model 3", "Електро", 250, 45000, 0.0));
        cars.add(createCar("BMW", "X5", "Дизель", 230, 60000, 9.5));
        cars.add(createCar("Audi", "A4", "Дизель", 220, 40000, 8.0));
        return cars;
    }

    private Car createCar(String make, String model, String fuelType, double maxSpeed, double price, double fuelConsumption) {
        Car car = mock(Car.class);
        when(car.getMake()).thenReturn(make);
        when(car.getModel()).thenReturn(model);
        when(car.getFuelType()).thenReturn(fuelType);
        when(car.getMaxSpeed()).thenReturn(maxSpeed);
        when(car.getPrice()).thenReturn(price);
        when(car.getFuelConsumption()).thenReturn(fuelConsumption);
        return car;
    }

    @Test
    void testLoadAndSortCars() {
        carFilterService.loadAndSortCars();
        verify(taxiFleet).loadCarsFromDatabase();
    }

    @Test
    void testApplyFiltersAndSorting_NoFilters() {
        List<Car> result = carFilterService.applyFiltersAndSorting("", "Усі", null, null, "");
        assertEquals(5, result.size());
    }

    @Test
    void testApplyFiltersAndSorting_SearchQuery() {
        List<Car> result = carFilterService.applyFiltersAndSorting("Toy", "Усі", null, null, "");
        assertEquals(2, result.size());
        assertEquals("Toyota", result.get(0).getMake());
    }

    @Test
    void testApplyFiltersAndSorting_FuelFilter() {
        List<Car> result = carFilterService.applyFiltersAndSorting("", "Дизель", null, null, "");
        assertEquals(2, result.size());
        assertEquals("Дизель", result.get(0).getFuelType());
    }

    @Test
    void testApplyFiltersAndSorting_MinSpeedFilter() {
        List<Car> result = carFilterService.applyFiltersAndSorting("", "Усі", 220.0, null, "");
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(car -> car.getMaxSpeed() >= 220));
    }

    @Test
    void testApplyFiltersAndSorting_MaxSpeedFilter() {
        List<Car> result = carFilterService.applyFiltersAndSorting("", "Усі", null, 220.0, "");
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(car -> car.getMaxSpeed() <= 220));
    }

    @Test
    void testApplyFiltersAndSorting_CombinedFilters() {
        List<Car> result = carFilterService.applyFiltersAndSorting("", "Дизель", 210.0, 230.0, "");
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(car ->
                car.getFuelType().equals("Дизель") &&
                        car.getMaxSpeed() >= 210 &&
                        car.getMaxSpeed() <= 230
        ));
    }

    @Test
    void testSortCars_ByPriceAscending() {
        List<Car> result = carFilterService.sortCars(testCars, "За ціною (зростання)");
        assertEquals(22000, result.get(0).getPrice());
        assertEquals(60000, result.get(4).getPrice());
    }

    @Test
    void testSortCars_ByPriceDescending() {
        List<Car> result = carFilterService.sortCars(testCars, "За ціною (спадання)");
        assertEquals(60000, result.get(0).getPrice());
        assertEquals(22000, result.get(4).getPrice());
    }

    @Test
    void testSortCars_BySpeedAscending() {
        List<Car> result = carFilterService.sortCars(testCars, "За швидкістю (зростання)");
        assertEquals(190, result.get(0).getMaxSpeed());
        assertEquals(250, result.get(4).getMaxSpeed());
    }

    @Test
    void testSortCars_BySpeedDescending() {
        List<Car> result = carFilterService.sortCars(testCars, "За швидкістю (спадання)");
        assertEquals(250, result.get(0).getMaxSpeed());
        assertEquals(190, result.get(4).getMaxSpeed());
    }

    @Test
    void testSortCars_ByConsumptionAscending() {
        List<Car> result = carFilterService.sortCars(testCars, "За витратою (зростання)");
        assertEquals(0.0, result.get(0).getFuelConsumption());
        assertEquals(9.5, result.get(4).getFuelConsumption());
    }

    @Test
    void testSortCars_ByConsumptionDescending() {
        List<Car> result = carFilterService.sortCars(testCars, "За витратою (спадання)");
        assertEquals(9.5, result.get(0).getFuelConsumption());
        assertEquals(0.0, result.get(4).getFuelConsumption());
    }

    @Test
    void testSortCars_DefaultCase() {
        List<Car> result = carFilterService.sortCars(testCars, "Невідомий критерій");
        assertEquals(5, result.size());
    }

    @Test
    void testFilterBySearchQuery() {
        List<Car> filtered = carFilterService.filterBySearchQuery(testCars, "Toy");
        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(car ->
                car.getMake().startsWith("Toyota") || car.getModel().startsWith("Toyota")
        ));
    }

    @Test
    void testFilterByFuelType() {
        List<Car> filtered = carFilterService.filterByFuelType(testCars, "Дизель");
        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(car -> car.getFuelType().equals("Дизель")));
    }

    @Test
    void testFilterByMinSpeed() {
        List<Car> filtered = carFilterService.filterByMinSpeed(testCars, 220);
        assertEquals(3, filtered.size());
        assertTrue(filtered.stream().allMatch(car -> car.getMaxSpeed() >= 220));
    }

    @Test
    void testFilterByMaxSpeed() {
        List<Car> filtered = carFilterService.filterByMaxSpeed(testCars, 220);
        assertEquals(3, filtered.size());
        assertTrue(filtered.stream().allMatch(car -> car.getMaxSpeed() <= 220));
    }
}