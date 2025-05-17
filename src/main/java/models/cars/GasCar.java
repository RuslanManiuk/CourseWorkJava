package models.cars;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Клас GasCar представляє автомобіль з двигуном внутрішнього згорання в системі.
 * Цей клас є розширенням абстрактного класу Car і призначений для роботи з
 * автомобілями, що використовують рідке паливо.
 */
public class GasCar extends Car {
    /** Логер для класу GasCar */
    private static final Logger logger = LogManager.getLogger(GasCar.class);

    /** Споживання палива в л/100км */
    private double fuelConsumption;

    /** Тип палива (бензин, дизель тощо) */
    private String fuelType;

    /**
     * Конструктор для створення нового об'єкту автомобіля з двигуном внутрішнього згорання.
     *
     * @param make            Виробник автомобіля
     * @param model           Модель автомобіля
     * @param price           Ціна автомобіля
     * @param maxSpeed        Максимальна швидкість автомобіля
     * @param fuelConsumption Споживання палива (л/100км)
     * @param fuelType        Тип палива (бензин, дизель тощо)
     */
    public GasCar(String make, String model, double price,
                  double maxSpeed, double fuelConsumption, String fuelType) {
        super(make, model, price, maxSpeed);
        logger.debug("Creating GasCar: {}, consumption: {} l/100km", fuelType, fuelConsumption);
        this.fuelConsumption = fuelConsumption;
        this.fuelType = fuelType;
    }

    /**
     * Повертає тип палива для автомобіля з двигуном внутрішнього згорання.
     *
     * @return Рядок, що представляє тип палива (бензин, дизель тощо)
     */
    @Override
    public String getFuelType() {
        return fuelType;
    }

    /**
     * Повертає споживання палива автомобіля з двигуном внутрішнього згорання.
     *
     * @return Значення споживання палива в л/100км
     */
    @Override
    public double getFuelConsumption() {
        return fuelConsumption;
    }
}