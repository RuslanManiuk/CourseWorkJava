package models.cars;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Car {
    private static final Logger logger = LogManager.getLogger(Car.class);

    private int carId;
    private String make;
    private String model;
    private double price;
    private double maxSpeed;

    public Car(String make, String model, double price, double maxSpeed) {
        logger.debug("Creating new Car: {} {}, price: {}, maxSpeed: {}", make, model, price, maxSpeed);
        this.make = make;
        this.model = model;
        this.price = price;
        this.maxSpeed = maxSpeed;
    }

    public abstract String getFuelType();
    public abstract double getFuelConsumption();

    public String getMake() { return make; }
    public String getModel() { return model; }
    public double getPrice() { return price; }
    public double getMaxSpeed() { return maxSpeed; }
    public int getCarId() { return carId; }
    public void setCarId(int id) {
        logger.trace("Setting car ID from {} to {}", this.carId, id);
        this.carId = id;
    }


    @Override
    public String toString() {
        return getFuelType() + " Марка " + make + " " + model +
                ", Ціна: " + price + "$, " +
                "Витрата пального: " + getFuelConsumption() +
                (getFuelType().equals("Електричний") ? " кВт·год/100км" : "л/100км") + ", " +
                "Макс. швидкість: " + maxSpeed + "км/год";
    }
}
