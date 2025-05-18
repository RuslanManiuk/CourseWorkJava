package models.cars;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GasCarTest {
    private GasCar gasCar;

    @BeforeEach
    void setUp() {
        gasCar = new GasCar("Toyota", "Camry", 30000.0, 210.0, 8.5, "Бензин");
    }

    @Test
    void testConstructorAndInheritedGetters() {
        assertEquals("Toyota", gasCar.getMake());
        assertEquals("Camry", gasCar.getModel());
        assertEquals(30000.0, gasCar.getPrice(), 0.001);
        assertEquals(210.0, gasCar.getMaxSpeed(), 0.001);
    }

    @Test
    void testFuelSpecificFields() {
        assertEquals(8.5, gasCar.getFuelConsumption(), 0.001);
        assertEquals("Бензин", gasCar.getFuelType());
    }

    @Test
    void testToString() {
        gasCar.setCarId(789);
        String expected = "Бензин Марка Toyota Camry, Ціна: 30000.0$, " +
                "Витрата пального: 8.5л/100км, " +
                "Макс. швидкість: 210.0км/год";
        assertEquals(expected, gasCar.toString());
    }

    @Test
    void testConstructorWithDifferentFuelType() {
        GasCar dieselCar = new GasCar("Volkswagen", "Passat", 35000.0, 220.0, 6.2, "Дизель");
        assertEquals("Дизель", dieselCar.getFuelType());
        assertEquals(6.2, dieselCar.getFuelConsumption(), 0.001);
    }

    @Test
    void testLoggingInConstructor() {
        assertDoesNotThrow(() -> new GasCar("Honda", "Accord", 28000.0, 200.0, 7.8, "Бензин"));
    }
}