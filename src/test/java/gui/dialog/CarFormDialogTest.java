package gui.dialog;

import gui.panel.CarListPanel;
import models.TaxiFleet;
import models.cars.ElectricCar;
import models.cars.GasCar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarFormDialogTest {

    private TaxiFleet fleet;
    private CarListPanel carListPanel;
    private JFrame parentFrame;
    private CarFormDialog dialog;

    @BeforeEach
    void setUp() {
        fleet = new TaxiFleet("Test Fleet");
        carListPanel = mock(CarListPanel.class);
        parentFrame = new JFrame();
        dialog = new CarFormDialog(parentFrame, fleet, carListPanel);
        dialog.setVisible(false); // Щоб тести не блокувалися
    }

    @Test
    void testInitialization() {
        assertNotNull(dialog);
        assertEquals("Додати новий автомобіль", dialog.getTitle());
        assertFalse(dialog.isResizable());
        assertTrue(dialog.isModal());
    }

    @Test
    void testCreateHeaderPanel() {
        JPanel headerPanel = dialog.createHeaderPanel();
        assertNotNull(headerPanel);
        assertEquals(1, headerPanel.getComponentCount());
        assertTrue(headerPanel.getComponent(0) instanceof JLabel);

        JLabel label = (JLabel) headerPanel.getComponent(0);
        assertEquals("Додати нове авто", label.getText());
        assertEquals(new Color(60, 90, 153), headerPanel.getBackground());
    }

    @Test
    void testCreateFormPanel() {
        JPanel formPanel = dialog.createFormPanel();
        assertNotNull(formPanel);
        assertTrue(formPanel.getComponentCount() > 0);
    }

    @Test
    void testCreateButtonPanel() {
        JPanel buttonPanel = dialog.createButtonPanel();
        assertNotNull(buttonPanel);
        assertEquals(3, buttonPanel.getComponentCount());
        assertTrue(buttonPanel.getComponent(0) instanceof JButton);
        assertTrue(buttonPanel.getComponent(1) instanceof JButton);
        assertTrue(buttonPanel.getComponent(2) instanceof JButton);
    }

    @Test
    void testUpdateFuelTypeVisibility() {
        // Перевіряємо початковий стан (бензиновий автомобіль)
        assertTrue(dialog.fuelTypeComboBox.isVisible());

        // Змінюємо на електричний
        dialog.typeComboBox.setSelectedIndex(1);
        dialog.updateFuelTypeVisibility(null);
        assertFalse(dialog.fuelTypeComboBox.isVisible());

        // Повертаємо на бензиновий
        dialog.typeComboBox.setSelectedIndex(0);
        dialog.updateFuelTypeVisibility(null);
        assertTrue(dialog.fuelTypeComboBox.isVisible());
    }

    @Test
    void testClearForm() {
        // Заповнюємо поля
        dialog.makeField.setText("Test");
        dialog.modelField.setText("Model");
        dialog.priceField.setText("10000");
        dialog.speedField.setText("200");
        dialog.consumptionField.setText("5");
        dialog.typeComboBox.setSelectedIndex(1);

        // Очищаємо форму
        dialog.clearForm();

        // Перевіряємо, що поля очищені
        assertEquals("", dialog.makeField.getText());
        assertEquals("", dialog.modelField.getText());
        assertEquals("", dialog.priceField.getText());
        assertEquals("", dialog.speedField.getText());
        assertEquals("", dialog.consumptionField.getText());
        assertEquals(0, dialog.typeComboBox.getSelectedIndex());
    }

    @Test
    void testAddGasCarSuccess() {
        // Налаштовуємо моки
        doNothing().when(carListPanel).loadAndSortCars();

        // Заповнюємо форму для бензинового авто
        dialog.makeField.setText("Toyota");
        dialog.modelField.setText("Camry");
        dialog.priceField.setText("25000");
        dialog.speedField.setText("180");
        dialog.consumptionField.setText("8.5");
        dialog.typeComboBox.setSelectedIndex(0);
        dialog.fuelTypeComboBox.setSelectedIndex(0); // Бензин

        // Викликаємо додавання авто
        dialog.addCar(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));

        // Перевіряємо, що авто додано
        assertEquals(1, fleet.getCars().size());
        assertTrue(fleet.getCars().get(0) instanceof GasCar);
        verify(carListPanel, times(1)).loadAndSortCars();
    }

    @Test
    void testAddElectricCarSuccess() {
        // Налаштовуємо моки
        doNothing().when(carListPanel).loadAndSortCars();

        // Заповнюємо форму для електричного авто
        dialog.makeField.setText("Tesla");
        dialog.modelField.setText("Model 3");
        dialog.priceField.setText("45000");
        dialog.speedField.setText("220");
        dialog.consumptionField.setText("15.5");
        dialog.typeComboBox.setSelectedIndex(1); // Електричний

        // Викликаємо додавання авто
        dialog.addCar(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));

        // Перевіряємо, що авто додано
        assertEquals(1, fleet.getCars().size());
        assertTrue(fleet.getCars().get(0) instanceof ElectricCar);
        verify(carListPanel, times(1)).loadAndSortCars();
    }

    @Test
    void testAddCarEmptyFields() {
        // Налаштовуємо моки
        doNothing().when(carListPanel).loadAndSortCars();

        // Залишаємо поля порожніми
        dialog.makeField.setText("");
        dialog.modelField.setText("");

        // Викликаємо додавання авто
        dialog.addCar(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));

        // Перевіряємо, що авто не додано
        assertEquals(0, fleet.getCars().size());
        verify(carListPanel, never()).loadAndSortCars();
    }

    @Test
    void testAddCarInvalidNumberFormat() {
        // Налаштовуємо моки
        doNothing().when(carListPanel).loadAndSortCars();

        // Вводимо некоректні числові значення
        dialog.makeField.setText("Toyota");
        dialog.modelField.setText("Camry");
        dialog.priceField.setText("invalid");
        dialog.speedField.setText("180");
        dialog.consumptionField.setText("8.5");

        // Викликаємо додавання авто
        dialog.addCar(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));

        // Перевіряємо, що авто не додано
        assertEquals(0, fleet.getCars().size());
        verify(carListPanel, never()).loadAndSortCars();
    }

    @Test
    void testUpdateConsumptionLabel() {
        JPanel testPanel = new JPanel();
        JLabel consumptionLabel = new JLabel("Витрата:");
        testPanel.add(consumptionLabel);

        // Перевіряємо оновлення для електричного авто
        dialog.updateConsumptionLabel(testPanel, true);
        assertEquals("Витрата (кВт·год/100км):", consumptionLabel.getText());

        // Перевіряємо оновлення для бензинового авто
        dialog.updateConsumptionLabel(testPanel, false);
        assertEquals("Витрата (л/100км):", consumptionLabel.getText());
    }

    @Test
    void testCreateFormLabel() {
        JLabel label = dialog.createFormLabel("Test Label");
        assertNotNull(label);
        assertEquals("Test Label", label.getText());
        assertEquals(new Font("Arial", Font.PLAIN, 13), label.getFont());
    }

    @Test
    void testCreateFormTextField() {
        JTextField textField = dialog.createFormTextField();
        assertNotNull(textField);
        assertEquals(new Font("Arial", Font.PLAIN, 13), textField.getFont());
        assertEquals(new Dimension(200, 30), textField.getPreferredSize());
    }
}