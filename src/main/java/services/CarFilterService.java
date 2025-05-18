package services;

import models.TaxiFleet;
import models.cars.Car;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Клас для фільтрації та сортування автомобілів таксі.
 * Надає функціонал для пошуку, фільтрації та сортування автомобілів у таксопарку.
 */
public class CarFilterService {
    // Таксопарк, з яким працює сервіс
    private final TaxiFleet taxiFleet;

    /**
     * Конструктор класу CarFilterService.
     *
     * @param taxiFleet таксопарк, з яким буде працювати сервіс
     */
    public CarFilterService(TaxiFleet taxiFleet) {
        this.taxiFleet = taxiFleet;
    }

    /**
     * Завантажує автомобілі з бази даних.
     */
    public void loadAndSortCars() {
        taxiFleet.loadCarsFromDatabase();
    }

    /**
     * Застосовує всі фільтри та сортування до списку автомобілів.
     *
     * @param searchQuery     пошуковий запит для марки або моделі автомобіля
     * @param currentFuelFilter тип палива для фільтрації
     * @param minSpeedFilter    мінімальна швидкість для фільтрації
     * @param maxSpeedFilter    максимальна швидкість для фільтрації
     * @param currentSort       критерій сортування
     * @return відфільтрований та відсортований список автомобілів
     */
    public List<Car> applyFiltersAndSorting(String searchQuery, String currentFuelFilter,
                                            Double minSpeedFilter, Double maxSpeedFilter,
                                            String currentSort) {
        List<Car> cars = new ArrayList<>(taxiFleet.getCars());

        if (!searchQuery.isEmpty()) {
            cars = filterBySearchQuery(cars, searchQuery);
        }

        if (!"Усі".equals(currentFuelFilter)) {
            cars = filterByFuelType(cars, currentFuelFilter);
        }

        if (minSpeedFilter != null) {
            cars = filterByMinSpeed(cars, minSpeedFilter);
        }

        if (maxSpeedFilter != null) {
            cars = filterByMaxSpeed(cars, maxSpeedFilter);
        }

        return sortCars(cars, currentSort);
    }

    /**
     * Фільтрує автомобілі за пошуковим запитом у назві марки або моделі.
     *
     * @param cars список автомобілів для фільтрації
     * @param searchQuery пошуковий запит
     * @return відфільтрований список автомобілів
     */
    List<Car> filterBySearchQuery(List<Car> cars, String searchQuery) {
        List<Car> filtered = new ArrayList<>();
        String query = searchQuery.toLowerCase();

        for (Car car : cars) {
            String make = car.getMake().toLowerCase();
            String model = car.getModel().toLowerCase();

            if (make.startsWith(query) ||
                    model.startsWith(query) ||
                    make.contains(" " + query) ||
                    model.contains(" " + query)) {
                filtered.add(car);
            }
        }
        return filtered;
    }

    /**
     * Фільтрує автомобілі за типом палива.
     *
     * @param cars список автомобілів для фільтрації
     * @param fuelType тип палива для фільтрації
     * @return відфільтрований список автомобілів
     */
    List<Car> filterByFuelType(List<Car> cars, String fuelType) {
        List<Car> filtered = new ArrayList<>();
        for (Car car : cars) {
            if (car.getFuelType().equals(fuelType)) {
                filtered.add(car);
            }
        }
        return filtered;
    }

    /**
     * Фільтрує автомобілі за мінімальною швидкістю.
     *
     * @param cars список автомобілів для фільтрації
     * @param minSpeed мінімальна швидкість
     * @return відфільтрований список автомобілів
     */
    List<Car> filterByMinSpeed(List<Car> cars, double minSpeed) {
        List<Car> filtered = new ArrayList<>();
        for (Car car : cars) {
            if (car.getMaxSpeed() >= minSpeed) {
                filtered.add(car);
            }
        }
        return filtered;
    }

    /**
     * Фільтрує автомобілі за максимальною швидкістю.
     *
     * @param cars список автомобілів для фільтрації
     * @param maxSpeed максимальна швидкість
     * @return відфільтрований список автомобілів
     */
    List<Car> filterByMaxSpeed(List<Car> cars, double maxSpeed) {
        List<Car> filtered = new ArrayList<>();
        for (Car car : cars) {
            if (car.getMaxSpeed() <= maxSpeed) {
                filtered.add(car);
            }
        }
        return filtered;
    }

    /**
     * Сортує автомобілі за вказаним критерієм.
     *
     * @param cars список автомобілів для сортування
     * @param sortCriteria критерій сортування
     * @return відсортований список автомобілів
     */
    public List<Car> sortCars(List<Car> cars, String sortCriteria) {
        List<Car> sortedCars = new ArrayList<>(cars);

        switch (sortCriteria) {
            case "За ціною (зростання)":
                sortedCars.sort(Comparator.comparingDouble(Car::getPrice));
                break;
            case "За ціною (спадання)":
                sortedCars.sort((c1, c2) -> Double.compare(c2.getPrice(), c1.getPrice()));
                break;
            case "За швидкістю (зростання)":
                sortedCars.sort(Comparator.comparingDouble(Car::getMaxSpeed));
                break;
            case "За швидкістю (спадання)":
                sortedCars.sort((c1, c2) -> Double.compare(c2.getMaxSpeed(), c1.getMaxSpeed()));
                break;
            case "За витратою (зростання)":
                sortedCars.sort(Comparator.comparingDouble(Car::getFuelConsumption));
                break;
            case "За витратою (спадання)":
                sortedCars.sort((c1, c2) -> Double.compare(c2.getFuelConsumption(), c1.getFuelConsumption()));
                break;
            default:
                break;
        }

        return sortedCars;
    }
}