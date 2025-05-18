package models.cars;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ElectricCarTest {
    private ElectricCar electricCar;

    @BeforeEach
    void setUp() {
        electricCar = new ElectricCar("Tesla", "Model 3", 45000.0, 250.0, 15.5);
    }

    @Test
    void testConstructorAndInheritedGetters() {
        assertEquals("Tesla", electricCar.getMake());
        assertEquals("Model 3", electricCar.getModel());
        assertEquals(45000.0, electricCar.getPrice(), 0.001);
        assertEquals(250.0, electricCar.getMaxSpeed(), 0.001);
    }

    @Test
    void testEnergyConsumptionField() {
        assertEquals(15.5, electricCar.getFuelConsumption(), 0.001);
    }

    @Test
    void testGetFuelType() {
        assertEquals("Електричний", electricCar.getFuelType());
    }

    @Test
    void testToString() {
        electricCar.setCarId(456);
        String expected = "Електричний Марка Tesla Model 3, Ціна: 45000.0$, " +
                "Витрата пального: 15.5 кВт·год/100км, " +
                "Макс. швидкість: 250.0км/год";
        assertEquals(expected, electricCar.toString());
    }

    @Test
    void testLoggingInConstructor() {
        // Просто перевіряємо, що конструктор не викидає винятків
        // Для повноцінного тестування логування потрібно використовувати Mockito окремо
        assertDoesNotThrow(() -> new ElectricCar("Nissan", "Leaf", 30000.0, 180.0, 17.2));
    }
}