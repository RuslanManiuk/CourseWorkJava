package models.cars;

public abstract class Car {
    private int carId;
    private String make;
    private String model;
    private double price;
    private double maxSpeed;

    public Car(String make, String model, double price, double maxSpeed) {
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
    public void setCarId(int id) { this.carId = id; }


    @Override
    public String toString() {
        return getFuelType() + " Марка " + make + " " + model +
                ", Ціна: " + price + "$, " +
                "Витрата пального: " + getFuelConsumption() +
                (getFuelType().equals("Електричний") ? " кВт·год/100км" : "л/100км") + ", " +
                "Макс. швидкість: " + maxSpeed + "км/год";
    }
}
