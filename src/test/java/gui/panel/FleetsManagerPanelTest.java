package gui.panel;

import models.TaxiFleet;
import managers.TaxiFleetManager;
import models.cars.GasCar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FleetsManagementPanelTest {

    private TaxiFleetManager fleetManager;
    private JTabbedPane mainTabbedPane;
    private FleetsManagementPanel panel;

    @BeforeEach
    void setUp() {
        fleetManager = mock(TaxiFleetManager.class);
        mainTabbedPane = mock(JTabbedPane.class);
        panel = new FleetsManagementPanel(fleetManager, mainTabbedPane);
    }

    @Test
    void testAddNewFleet() {
        try (MockedStatic<JOptionPane> mockedJOptionPane = mockStatic(JOptionPane.class)) {
            // Налаштування моку для showInputDialog
            mockedJOptionPane.when(() -> JOptionPane.showInputDialog(
                    any(Component.class),
                    anyString(),
                    anyString(),
                    eq(JOptionPane.PLAIN_MESSAGE))
            ).thenReturn("New Fleet");

            // Виклик методу
            panel.addNewFleet();

            // Перевірка, що fleetManager.addFleet() був викликаний
            verify(fleetManager).addFleet(any(TaxiFleet.class));
        }
    }

    @Test
    void testRemoveSelectedFleet() {
        // 1. Prepare test data
        TaxiFleet fleet = new TaxiFleet("Test Fleet");

        // 2. Set up mock behavior - return our test fleet
        when(fleetManager.getFleets()).thenReturn(Arrays.asList(fleet));

        // 3. Update the panel's list to match the mock
        panel.updateFleetsList();

        // 4. Get the JList and select the item
        JList<TaxiFleet> fleetsList = (JList<TaxiFleet>) TestUtils.getField(panel, "fleetsList");
        fleetsList.setSelectedValue(fleet, true);

        // 5. Mock confirmation dialog
        try (MockedStatic<JOptionPane> mockedJOptionPane = mockStatic(JOptionPane.class)) {
            mockedJOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    any(Component.class),
                    anyString(),
                    anyString(),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.WARNING_MESSAGE))
            ).thenReturn(JOptionPane.YES_OPTION);

            // 6. Call the method under test
            panel.removeSelectedFleet();

            // 7. Verify removeFleet was called with our test fleet
            verify(fleetManager).removeFleet(fleet);
        }
    }

    @Test
    void testEditFleet() {
        TaxiFleet fleet = new TaxiFleet("Old Name");

        try (MockedStatic<JOptionPane> mockedJOptionPane = mockStatic(JOptionPane.class)) {
            // Налаштування моку для showInputDialog
            mockedJOptionPane.when(() -> JOptionPane.showInputDialog(
                    any(Component.class),
                    anyString(),
                    anyString(),
                    eq(JOptionPane.PLAIN_MESSAGE))
            ).thenReturn("New Name");

            // Виклик методу
            panel.editFleet(fleet);

            // Перевірка, що ім'я змінилося
            assertEquals("New Name", fleet.getName());
        }
    }

    @Test
    void testViewCars() {
        TaxiFleet fleet = new TaxiFleet("Test Fleet");

        // Виклик методу
        panel.viewCars(fleet);

        // Перевірка, що була спроба додати нову вкладку
        verify(mainTabbedPane).addTab(eq("Test Fleet"), any(FleetManagementPanel.class));
        verify(mainTabbedPane).setSelectedComponent(any(FleetManagementPanel.class));
    }

    @Test
    void testDarkenColor() {
        Color original = new Color(100, 100, 100);
        Color darkened = panel.darkenColor(original, 0.1f);

        assertTrue(darkened.getRed() < original.getRed());
        assertTrue(darkened.getGreen() < original.getGreen());
        assertTrue(darkened.getBlue() < original.getBlue());
    }

    @Test
    void testFleetListCellRenderer() {
        FleetsManagementPanel.FleetListCellRenderer renderer =
                new FleetsManagementPanel.FleetListCellRenderer();

        TaxiFleet fleet = new TaxiFleet("Test Fleet");
        // Створюємо реальний об'єкт GasCar з правильними параметрами
        GasCar car = new GasCar("Toyota", "Camry", 25000.0, 180.0, 8.5, "Бензин");
        fleet.getCars().add(car);

        // Тестуємо рендерер без виділення
        Component component = renderer.getListCellRendererComponent(
                new JList<>(), fleet, 0, false, false);
        assertTrue(component instanceof JPanel);

        // Тестуємо рендерер з виділенням
        component = renderer.getListCellRendererComponent(
                new JList<>(), fleet, 0, true, false);
        assertEquals(FleetsManagementPanel.SECONDARY_COLOR, component.getBackground());
    }

    @Test
    void testMouseEnteredAndExited() {
        // Підготовка кнопки
        JButton button = panel.createStyledButton("Test", Color.BLUE);

        // Симуляція події наведення миші
        MouseEvent enterEvent = new MouseEvent(
                button, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(),
                0, 0, 0, 0, false);
        button.dispatchEvent(enterEvent);

        // Перевірка, що колір змінився
        assertNotEquals(Color.BLUE, button.getBackground());

        // Симуляція події відведення миші
        MouseEvent exitEvent = new MouseEvent(
                button, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(),
                0, 0, 0, 0, false);
        button.dispatchEvent(exitEvent);

        // Перевірка, що колір повернувся
        assertEquals(Color.BLUE, button.getBackground());
    }

    @Test
    void testCreateFleetInfoPanel() {
        TaxiFleet fleet = new TaxiFleet("Test Fleet");
        JPanel infoPanel = panel.createFleetInfoPanel(fleet);

        assertNotNull(infoPanel);
        assertEquals(2, infoPanel.getComponentCount());
    }
}

class TestUtils {
    public static Object getField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}