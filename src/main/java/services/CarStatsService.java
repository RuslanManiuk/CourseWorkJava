package services;

import models.TaxiFleet;
import models.cars.Car;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Клас для обчислення статистики автомобілів таксопарку.
 * Надає функціонал для розрахунку середнього споживання палива різних типів автомобілів.
 */
public class CarStatsService {
    // Таксопарк, з яким працює сервіс
    private final TaxiFleet taxiFleet;

    /**
     * Конструктор класу CarStatsService.
     *
     * @param taxiFleet таксопарк, з яким буде працювати сервіс
     */
    public CarStatsService(TaxiFleet taxiFleet) {
        this.taxiFleet = taxiFleet;
    }

    /**
     * Обчислює загальну вартість усіх автомобілів у таксопарку.
     *
     * @return загальна вартість автопарку
     */
    public double calculateTotalCost() {
        return taxiFleet.getCars().stream()
                .mapToDouble(Car::getPrice)
                .sum();
    }

    /**
     * Обчислює середнє споживання електроенергії для електромобілів.
     *
     * @return середнє споживання електроенергії (кВт·год/100 км) або 0, якщо електромобілів немає
     */
    public double calculateAverageElectricConsumption() {
        List<Car> electricCars = taxiFleet.getCars().stream()
                .filter(c -> "Електричний".equals(c.getFuelType()))
                .collect(Collectors.toList());

        if (electricCars.isEmpty()) {
            return 0;
        }

        return electricCars.stream()
                .mapToDouble(Car::getFuelConsumption)
                .average()
                .orElse(0);
    }

    /**
     * Обчислює середнє споживання пального для автомобілів з двигунами внутрішнього згоряння.
     *
     * @return середнє споживання пального (л/100 км) або 0, якщо таких автомобілів немає
     */
    public double calculateAverageGasConsumption() {
        List<Car> gasCars = taxiFleet.getCars().stream()
                .filter(c -> !"Електричний".equals(c.getFuelType()))
                .collect(Collectors.toList());

        if (gasCars.isEmpty()) {
            return 0;
        }

        return gasCars.stream()
                .mapToDouble(Car::getFuelConsumption)
                .average()
                .orElse(0);
    }
}