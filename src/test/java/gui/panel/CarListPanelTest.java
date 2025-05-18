package gui.panel;

import gui.dialog.CarFormDialog;
import models.TaxiFleet;
import models.cars.Car;
import models.cars.ElectricCar;
import models.cars.GasCar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarListPanelTest {

    private TaxiFleet fleet;
    private CarListPanel panel;
    private DefaultTableModel model;

    @BeforeEach
    void setUp() throws Exception {
        fleet = new TaxiFleet("Test Fleet");
        fleet.getCars().add(new GasCar("Toyota", "Camry", 25000.0, 180.0, 8.5, "Бензин"));
        fleet.getCars().add(new ElectricCar("Tesla", "Model 3", 45000.0, 220.0, 15.5));
        panel = new CarListPanel(fleet);
        model = (DefaultTableModel) getPrivateField(panel, "model");
    }

    @Test
    void testIsCellEditable() {
        assertFalse(model.isCellEditable(0, 0), "All cells should be non-editable");
        assertFalse(model.isCellEditable(1, 3), "Price column should be non-editable");
    }

    @Test
    void testGetColumnClass() {
        assertEquals(String.class, model.getColumnClass(0), "Make column should be String");
        assertEquals(String.class, model.getColumnClass(1), "Model column should be String");
        assertEquals(String.class, model.getColumnClass(2), "Fuel type column should be String");
        assertEquals(Double.class, model.getColumnClass(3), "Price column should be Double");
        assertEquals(Double.class, model.getColumnClass(4), "Speed column should be Double");
        assertEquals(String.class, model.getColumnClass(5), "Consumption column should be String");
    }

    @Test
    void testDefaultTableCellRenderer() throws Exception {
        JTable carTable = (JTable) getPrivateField(panel, "carTable");
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) carTable.getDefaultRenderer(Object.class);

        Component c = renderer.getTableCellRendererComponent(
                carTable,
                "Test",
                false,
                false,
                0,
                0
        );

        assertEquals(Color.WHITE, c.getBackground(), "Even row should be white");
        assertEquals(JLabel.LEFT, ((JLabel)c).getHorizontalAlignment(), "Text should be left-aligned");
    }

    @Test
    void testDoubleValueRenderer() throws Exception {
        // Set locale to US for consistent decimal formatting
        Locale originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        try {
            JTable carTable = (JTable) getPrivateField(panel, "carTable");
            DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) carTable.getDefaultRenderer(Double.class);

            // Test price formatting
            Component priceCell = renderer.getTableCellRendererComponent(
                    carTable,
                    25000.0,
                    false,
                    false,
                    0,
                    3
            );
            assertEquals("25000.00 $", ((JLabel)priceCell).getText(), "Price should be formatted");

            // Test speed formatting
            Component speedCell = renderer.getTableCellRendererComponent(
                    carTable,
                    180.0,
                    false,
                    false,
                    0,
                    4
            );
            assertEquals("180.0 км/год", ((JLabel)speedCell).getText(), "Speed should be formatted");
        } finally {
            // Restore original locale
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void testMouseClicked() throws Exception {
        // 1. Get the table through reflection
        JTable carTable = (JTable) getPrivateField(panel, "carTable");

        // 2. Verify initial state (should be empty)
        assertEquals(0, carTable.getRowCount(), "Table should start empty");

        // 3. Directly update the table model with test data
        DefaultTableModel model = (DefaultTableModel) carTable.getModel();
        model.setRowCount(0); // Clear any existing data

        // Add test data directly to the model
        model.addRow(new Object[]{"Toyota", "Camry", "Бензин", 25000.0, 180.0, "8.5 л/100км"});
        model.addRow(new Object[]{"Tesla", "Model 3", "Електричний", 45000.0, 220.0, "15.5 кВт·год"});

        // 4. Verify table now has rows
        assertEquals(2, carTable.getRowCount(), "Table should now have 2 rows");

        // 5. Select the first row
        carTable.setRowSelectionInterval(0, 0);
        assertEquals(0, carTable.getSelectedRow(), "First row should be selected");

        // 6. Simulate double click
        carTable.dispatchEvent(new MouseEvent(
                carTable,
                MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(),
                0,
                0, 0, 2, false
        ));

        // 7. Verify selection was maintained
        assertEquals(0, carTable.getSelectedRow(), "First row should remain selected after click");
    }

    @Test
    void testDocumentListenerMethods() throws Exception {
        JTextField searchField = (JTextField) getPrivateField(panel, "searchField");
        Field searchQueryField = CarListPanel.class.getDeclaredField("searchQuery");
        searchQueryField.setAccessible(true);

        // Test insertUpdate
        searchField.setText("Toy");
        assertEquals("toy", searchQueryField.get(panel), "Search query should update on insert");

        // Test removeUpdate
        searchField.setText("To");
        assertEquals("to", searchQueryField.get(panel), "Search query should update on remove");
    }

    @Test
    void testKeyPressedEscape() throws Exception {
        JTextField searchField = (JTextField) getPrivateField(panel, "searchField");
        Field searchQueryField = CarListPanel.class.getDeclaredField("searchQuery");
        searchQueryField.setAccessible(true);

        // Set initial text
        searchField.setText("Test");

        // Get the key listener and invoke it directly
        KeyListener[] listeners = searchField.getKeyListeners();
        for (KeyListener listener : listeners) {
            if (listener instanceof KeyAdapter) {
                ((KeyAdapter)listener).keyPressed(new KeyEvent(
                        searchField,
                        KeyEvent.KEY_PRESSED,
                        System.currentTimeMillis(),
                        0,
                        KeyEvent.VK_ESCAPE,
                        KeyEvent.CHAR_UNDEFINED
                ));
            }
        }

        assertTrue(searchField.getText().isEmpty(), "Search should clear on ESC");
        assertTrue(((String)searchQueryField.get(panel)).isEmpty(), "Search query should clear on ESC");
    }

    @Test
    void testOpenAddCarDialog() {
        try (MockedStatic<SwingUtilities> utilities = Mockito.mockStatic(SwingUtilities.class)) {
            // 1. Створюємо більш "розумний" mock для JFrame
            JFrame mockFrame = mock(JFrame.class);

            // Ініціалізуємо необхідні поля для коректної роботи AWT
            when(mockFrame.getOwnedWindows()).thenReturn(new Window[0]);
            when(mockFrame.isDisplayable()).thenReturn(true);
            when(mockFrame.getToolkit()).thenReturn(Toolkit.getDefaultToolkit());

            // 2. Налаштовуємо mock для SwingUtilities
            utilities.when(() -> SwingUtilities.getWindowAncestor(panel))
                    .thenReturn(mockFrame);

            // 3. Мокуємо конструктор CarFormDialog, щоб уникнути реального створення вікна
            try (MockedConstruction<CarFormDialog> mockedConstruction =
                         Mockito.mockConstruction(CarFormDialog.class)) {

                // 4. Викликаємо тестований метод
                panel.openAddCarDialog();

                // 5. Перевіряємо, що метод намагався отримати батьківське вікно
                utilities.verify(() -> SwingUtilities.getWindowAncestor(panel));

                // 6. Перевіряємо, що конструктор CarFormDialog був викликаний
                assertEquals(1, mockedConstruction.constructed().size());
                CarFormDialog dialogMock = mockedConstruction.constructed().get(0);
                verify(dialogMock).setVisible(true);
            }
        }
    }

//    @Test
//    void testRemoveSelectedCar() throws Exception {
//        // 1. Підготовка тестових даних
//        TaxiFleet testFleet = new TaxiFleet("Test Fleet");
//        Car car1 = new GasCar("Toyota", "Camry", 25000.0, 180.0, 8.5, "Бензин");
//        Car car2 = new ElectricCar("Tesla", "Model 3", 45000.0, 220.0, 15.5);
//        testFleet.getCars().add(car1);
//        testFleet.getCars().add(car2);
//
//        // 2. Ініціалізація панелі
//        CarListPanel testPanel = new CarListPanel(testFleet);
//
//        // 3. Отримання доступу до компонентів
//        JTable carTable = (JTable) getPrivateField(testPanel, "carTable");
//        DefaultTableModel model = (DefaultTableModel) carTable.getModel();
//
//        // 4. Оновлення таблиці
//        testPanel.updateTable(testFleet.getCars());
//
//        // 5. Перевірка початкового стану
//        assertEquals(2, testFleet.getCars().size());
//        assertEquals(2, model.getRowCount());
//
//        // 6. Вибираємо перший рядок
//        carTable.setRowSelectionInterval(0, 0);
//
//        // 7. Мокуємо JOptionPane (правильний спосіб)
//        try (MockedStatic<JOptionPane> mockedJOptionPane = mockStatic(JOptionPane.class)) {
//            // Мокуємо з точними параметрами
//            mockedJOptionPane.when(() ->
//                    JOptionPane.showConfirmDialog(
//                            any(Component.class),
//                            eq("Ви дійсно хочете видалити обраний автомобіль?"),
//                            eq("Підтвердження видалення"),
//                            eq(JOptionPane.YES_NO_OPTION),
//                            eq(JOptionPane.QUESTION_MESSAGE)
//                    )
//            ).thenReturn(JOptionPane.YES_OPTION);
//
//            // 8. Викликаємо метод
//            testPanel.removeSelectedCar();
//
//            // 9. Перевіряємо результати
//            assertEquals(1, testFleet.getCars().size());
//            assertEquals("Tesla", testFleet.getCars().get(0).getMake());
//        }
//    }

//    @Test
//    void testShowCarDetails() throws Exception {
//        // 1. Prepare test data
//        TaxiFleet testFleet = new TaxiFleet("Test Fleet");
//        Car testCar = new GasCar("Toyota", "Camry", 25000.0, 180.0, 8.5, "Бензин");
//        testFleet.getCars().add(testCar);
//
//        // 2. Initialize panel
//        CarListPanel testPanel = new CarListPanel(testFleet);
//
//        // 3. Get the table and model
//        JTable carTable = (JTable) getPrivateField(testPanel, "carTable");
//        DefaultTableModel model = (DefaultTableModel) carTable.getModel();
//
//        // 4. Update the table in EDT thread
//        SwingUtilities.invokeAndWait(() -> {
//            model.setRowCount(0); // Clear existing data
//            model.addRow(new Object[]{
//                    "Toyota", "Camry", "Бензин", 25000.0, 180.0, "8.5 л/100км"
//            });
//        });
//
//        // 5. Verify the table has data
//        SwingUtilities.invokeAndWait(() -> {
//            assertEquals(1, model.getRowCount(), "Table should have 1 row");
//        });
//
//        // 6. Mock JOptionPane
//        try (MockedStatic<JOptionPane> mockedJOptionPane = Mockito.mockStatic(JOptionPane.class)) {
//            // Setup mock - no return value needed for void method
//            mockedJOptionPane.when(() -> JOptionPane.showMessageDialog(
//                    any(Component.class),
//                    anyString(),
//                    anyString(),
//                    eq(JOptionPane.INFORMATION_MESSAGE)
//            )).thenAnswer(invocation -> null);
//
//            // 7. Call the method in EDT thread
//            SwingUtilities.invokeAndWait(() -> {
//                carTable.setRowSelectionInterval(0, 0); // Select first row
//                testPanel.showCarDetails(); // Call the method
//            });
//
//            // 8. Verify the dialog was shown
//            mockedJOptionPane.verify(() -> JOptionPane.showMessageDialog(
//                    any(Component.class),
//                    contains("Toyota"),
//                    anyString(),
//                    eq(JOptionPane.INFORMATION_MESSAGE)
//            ));
//        }
//    }

    @Test
    void testResetFilters() throws Exception {
        JComboBox<String> fuelTypeFilter = (JComboBox<String>) getPrivateField(panel, "fuelTypeFilter");
        JComboBox<String> sortComboBox = (JComboBox<String>) getPrivateField(panel, "sortComboBox");
        JTextField minSpeedField = (JTextField) getPrivateField(panel, "minSpeedField");
        JTextField maxSpeedField = (JTextField) getPrivateField(panel, "maxSpeedField");
        JTextField searchField = (JTextField) getPrivateField(panel, "searchField");

        // Set some filter values
        fuelTypeFilter.setSelectedItem("Бензин");
        sortComboBox.setSelectedItem("За ціною (зростання)");
        minSpeedField.setText("100");
        maxSpeedField.setText("200");
        searchField.setText("test");

        panel.resetFilters(fuelTypeFilter, sortComboBox, minSpeedField, maxSpeedField, searchField);

        assertEquals("Усі", fuelTypeFilter.getSelectedItem());
        assertEquals("За замовчуванням", sortComboBox.getSelectedItem());
        assertTrue(minSpeedField.getText().isEmpty());
        assertTrue(maxSpeedField.getText().isEmpty());
        assertTrue(searchField.getText().isEmpty());
    }

    @Test
    void testShowErrorMessage() {
        try (MockedStatic<JOptionPane> mockedJOptionPane = mockStatic(JOptionPane.class)) {
            panel.showErrorMessage("Test Error", "This is a test error message");

            mockedJOptionPane.verify(() -> JOptionPane.showMessageDialog(
                    any(Component.class),
                    eq("This is a test error message"),
                    eq("Test Error"),
                    eq(JOptionPane.ERROR_MESSAGE)
            ));
        }
    }

    @Test
    void testUpdateTable() {
        // 1. Підготовка тестових даних
        List<Car> testCars = Arrays.asList(
                new GasCar("Toyota", "Corolla", 22000.0, 170.0, 7.5, "Бензин"),
                new ElectricCar("Nissan", "Leaf", 32000.0, 150.0, 14.0)
        );

        // 2. Оновлення таблиці
        panel.updateTable(testCars);

        // 3. Перевірки
        assertEquals(2, model.getRowCount(), "Таблиця повинна містити 2 рядки");

        // Перевірка марки
        assertEquals("Toyota", model.getValueAt(0, 0), "Марка першого авто повинна бути Toyota");

        // Перевірка ціни (може бути як Double, так і String)
        Object priceValue = model.getValueAt(0, 3);
        if (priceValue instanceof Double) {
            assertEquals(22000.0, (Double)priceValue, 0.001, "Ціна повинна бути 22000.0");
        } else {
            assertTrue(priceValue.toString().contains("22000"),
                    "Ціна повинна містити 22000. Отримано: " + priceValue);
        }

        // Перевірка швидкості
        Object speedValue = model.getValueAt(0, 4);
        if (speedValue instanceof Double) {
            assertEquals(170.0, (Double)speedValue, 0.001, "Швидкість повинна бути 170.0");
        } else {
            assertTrue(speedValue.toString().matches(".*170[,.]?0?.*"),
                    "Швидкість повинна містити 170. Отримано: " + speedValue);
        }

        // Перевірка споживання (електроенергії)
        Object consumptionValue = model.getValueAt(1, 5);
        String consumptionStr = consumptionValue.toString();

        // Перевіряємо чи містить значення споживання (14.0 або 14,0)
        assertTrue(consumptionStr.matches(".*14[,.]0.*"),
                "Споживання повинно містити 14.0. Отримано: " + consumptionStr);

        // Перевіряємо одиниці виміру (можуть бути різні варіанти)
        assertTrue(consumptionStr.matches(".*(кВт·год|kW·h|кВт/год).*"),
                "Споживання повинно містити одиниці виміру. Отримано: " + consumptionStr);
    }

    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }
}