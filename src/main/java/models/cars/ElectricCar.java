package models.cars;

public class ElectricCar extends Car {
    private double energyConsumption; // кВт·год/100км

    public ElectricCar(String make, String model, double price,
                       double maxSpeed, double energyConsumption) {
        super(make, model, price, maxSpeed);
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