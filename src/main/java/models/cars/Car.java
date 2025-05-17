package models.cars;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Абстрактний базовий клас, що представляє автомобіль із загальними властивостями та поведінкою.
 * Цей клас забезпечує основу для різних типів автомобілів у системі.
 */
public abstract class Car {
    private static final Logger logger = LogManager.getLogger(Car.class);

    private int carId;
    private String make;
    private String model;
    private double price;
    private double maxSpeed;

    /**
     * Створює новий автомобіль із вказаними параметрами.
     *
     * @param make      Виробник автомобіля
     * @param model     Модель автомобіля
     * @param price     Ціна автомобіля в доларах
     * @param maxSpeed  Максимальна швидкість автомобіля в км/год
     */
    public Car(String make, String model, double price, double maxSpeed) {
        logger.debug("Creating new Car: {} {}, price: {}, maxSpeed: {}", make, model, price, maxSpeed);
        this.make = make;
        this.model = model;
        this.price = price;
        this.maxSpeed = maxSpeed;
    }

    // Абстрактні методи, які повинні бути реалізовані в підкласах
    /**
     * Отримує тип палива, що використовується цим автомобілем.
     *
     * @return Тип палива у вигляді рядка
     */
    public abstract String getFuelType();

    /**
     * Отримує показник витрати палива цього автомобіля.
     *
     * @return Витрата палива в л/100км або кВт·год/100км
     */
    public abstract double getFuelConsumption();

    // Геттери та сеттери
    /**
     * Отримує виробника автомобіля.
     *
     * @return Виробник автомобіля
     */
    public String getMake() {
        return make;
    }

    /**
     * Отримує модель автомобіля.
     *
     * @return Модель автомобіля
     */
    public String getModel() {
        return model;
    }

    /**
     * Отримує ціну автомобіля.
     *
     * @return Ціна в доларах
     */
    public double getPrice() {
        return price;
    }

    /**
     * Отримує максимальну швидкість автомобіля.
     *
     * @return Максимальна швидкість в км/год
     */
    public double getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Отримує унікальний ідентифікатор автомобіля.
     *
     * @return Ідентифікатор автомобіля
     */
    public int getCarId() {
        return carId;
    }

    /**
     * Встановлює унікальний ідентифікатор для автомобіля.
     *
     * @param id Нове значення ідентифікатора автомобіля
     */
    public void setCarId(int id) {
        logger.trace("Setting car ID from {} to {}", this.carId, id);
        this.carId = id;
    }

    /**
     * Повертає рядкове представлення автомобіля з усією відповідною інформацією.
     * Вихідні дані включають тип палива, виробника, модель, ціну, витрату палива з відповідними одиницями
     * та максимальну швидкість.
     *
     * @return Форматований рядок, що описує автомобіль
     */
    @Override
    public String toString() {
        return getFuelType() + " Марка " + make + " " + model +
                ", Ціна: " + price + "$, " +
                "Витрата пального: " + getFuelConsumption() +
                (getFuelType().equals("Електричний") ? " кВт·год/100км" : "л/100км") + ", " +
                "Макс. швидкість: " + maxSpeed + "км/год";
    }
}