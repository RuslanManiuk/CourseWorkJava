package services;

import models.TaxiFleet;

/**
 * Клас для підрахунку кількості автомобілів різних типів у таксопарку.
 */
public class CarCountService {
    private final TaxiFleet taxiFleet;

    /**
     * Конструктор класу CarCountService.
     *
     * @param taxiFleet таксопарк, з яким буде працювати сервіс
     */
    public CarCountService(TaxiFleet taxiFleet) {
        this.taxiFleet = taxiFleet;
    }

    /**
     * Повертає кількість електричних автомобілів у таксопарку.
     *
     * @return кількість електричних автомобілів
     */
    public int getElectricCarCount() {
        return (int) taxiFleet.getCars().stream()
                .filter(c -> "Електричний".equals(c.getFuelType()))
                .count();
    }

    /**
     * Повертає кількість автомобілів з двигуном внутрішнього згоряння у таксопарку.
     *
     * @return кількість автомобілів з ДВЗ
     */
    public int getGasCarCount() {
        return taxiFleet.getCars().size() - getElectricCarCount();
    }

    /**
     * Повертає загальну кількість автомобілів у таксопарку.
     *
     * @return загальна кількість автомобілів
     */
    public int getTotalCarCount() {
        return taxiFleet.getCars().size();
    }
}