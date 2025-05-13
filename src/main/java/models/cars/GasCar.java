package models.cars;

public class GasCar extends Car {
    private double fuelConsumption;
    private String fuelType; // "Бензин" або "Дизель"

    public GasCar(String make, String model, double price,
                  double maxSpeed, double fuelConsumption, String fuelType) {
        super(make, model, price, maxSpeed);
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