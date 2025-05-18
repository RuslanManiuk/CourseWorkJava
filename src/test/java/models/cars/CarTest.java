package models.cars;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Тестовий підклас для абстрактного класу Car
class TestCar extends Car {
    public TestCar(String make, String model, double price, double maxSpeed) {
        super(make, model, price, maxSpeed);
    }

    @Override
    public String getFuelType() {
        return "Бензин";
    }

    @Override
    public double getFuelConsumption() {
        return 7.5;
    }
}

class CarTest {
    private TestCar testCar;

    @BeforeEach
    void setUp() {
        testCar = new TestCar("Toyota", "Camry", 25000.0, 210.0);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("Toyota", testCar.getMake());
        assertEquals("Camry", testCar.getModel());
        assertEquals(25000.0, testCar.getPrice(), 0.001);
        assertEquals(210.0, testCar.getMaxSpeed(), 0.001);
    }

    @Test
    void testSetCarId() {
        testCar.setCarId(123);
        assertEquals(123, testCar.getCarId());
    }

    @Test
    void testToString() {
        testCar.setCarId(123);
        String expected = "Бензин Марка Toyota Camry, Ціна: 25000.0$, Витрата пального: 7.5л/100км, Макс. швидкість: 210.0км/год";
        assertEquals(expected, testCar.toString());
    }

    @Test
    void testAbstractMethods() {
        assertEquals("Бензин", testCar.getFuelType());
        assertEquals(7.5, testCar.getFuelConsumption(), 0.001);
    }
}