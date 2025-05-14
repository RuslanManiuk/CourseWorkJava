package models.cars;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ElectricCar extends Car {
    private static final Logger logger = LogManager.getLogger(ElectricCar.class);
    private double energyConsumption; // кВт·год/100км

    public ElectricCar(String make, String model, double price,
                       double maxSpeed, double energyConsumption) {
        super(make, model, price, maxSpeed);
        logger.debug("Creating ElectricCar with consumption: {} kWh/100km", energyConsumption);
        this.energyConsumption = energyConsumption;
    }

    @Override
    public String getFuelType() {
        return "Електричний";
    }

    @Override
    public double getFuelConsumption() {
        return energyConsumption;
    }
}