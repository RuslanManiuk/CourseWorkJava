package models.cars;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Клас ElectricCar представляє електричний автомобіль в системі.
 * Цей клас є розширенням абстрактного класу Car і призначений для роботи з
 * електричними транспортними засобами.
 */
public class ElectricCar extends Car {
    // Логер для класу ElectricCar
    private static final Logger logger = LogManager.getLogger(ElectricCar.class);

    // Споживання енергії в кВт·год/100км
    private double energyConsumption;

    /**
     * Конструктор для створення нового об'єкту електричного автомобіля.
     *
     * @param make              Виробник автомобіля
     * @param model             Модель автомобіля
     * @param price             Ціна автомобіля
     * @param maxSpeed          Максимальна швидкість автомобіля
     * @param energyConsumption Споживання енергії (кВт·год/100км)
     */
    public ElectricCar(String make, String model, double price,
                       double maxSpeed, double energyConsumption) {
        super(make, model, price, maxSpeed);
        logger.debug("Creating ElectricCar with consumption: {} kWh/100km", energyConsumption);
        this.energyConsumption = energyConsumption;
    }

    /**
     * Повертає тип палива для електричного автомобіля.
     *
     * @return Рядок, що представляє тип палива ("Електричний")
     */
    @Override
    public String getFuelType() {
        return "Електричний";
    }

    /**
     * Повертає споживання енергії електричного автомобіля.
     *
     * @return Значення споживання енергії в кВт·год/100км
     */
    @Override
    public double getFuelConsumption() {
        return energyConsumption;
    }
}