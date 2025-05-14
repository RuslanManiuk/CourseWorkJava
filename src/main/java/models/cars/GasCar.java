package models.cars;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GasCar extends Car {
    private static final Logger logger = LogManager.getLogger(GasCar.class);
    private double fuelConsumption;
    private String fuelType;

    public GasCar(String make, String model, double price,
                  double maxSpeed, double fuelConsumption, String fuelType) {
        super(make, model, price, maxSpeed);
        logger.debug("Creating GasCar: {}, consumption: {} l/100km", fuelType, fuelConsumption);
        this.fuelConsumption = fuelConsumption;
        this.fuelType = fuelType;
    }

    @Override
    public String getFuelType() {
        return fuelType;
    }

    @Override
    public double getFuelConsumption() {
        return fuelConsumption;
    }
}