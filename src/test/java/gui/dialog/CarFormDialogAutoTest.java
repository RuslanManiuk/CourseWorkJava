package gui.dialog;

import models.TaxiFleet;
import models.cars.Car;
import models.cars.ElectricCar;
import models.cars.GasCar;
import gui.panel.CarListPanel;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import javax.swing.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Для ініціалізації моків через @Mock
public class CarFormDialogAutoTest { // Ваша назва класу

    private Robot robot;
    private DialogFixture window;
    private CarFormDialog dialog; // Сам об'єкт діалогу
    private TaxiFleet taxiFleet;

    @Mock
    private CarListPanel mockCarListPanel;

    private JFrame parentFrame;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install(); // Важливо для виявлення проблем з потоками Swing
    }

    @BeforeEach
    public void setUp() {
        // MockitoAnnotations.openMocks(this); // Не потрібно, якщо використовується @ExtendWith(MockitoExtension.class)
        robot = BasicRobot.robotWithCurrentAwtHierarchy(); // Створюємо робота

        // Створюємо компоненти в EDT
        parentFrame = GuiActionRunner.execute(() -> new JFrame());
        taxiFleet = new TaxiFleet("TestFleet");
        dialog = GuiActionRunner.execute(() -> new CarFormDialog(parentFrame, taxiFleet, mockCarListPanel));

        window = new DialogFixture(robot, dialog);
        window.show(); // Робимо діалог видимим для взаємодії
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp(); // Закриває діалог і звільняє ресурси fixture
        GuiActionRunner.execute(() -> {
            if (parentFrame != null) {
                parentFrame.dispose();
            }
        });
        robot.cleanUp(); // Очищення робота
    }

    @Test
    public void initialState_shouldHaveEmptyFieldsAndDefaultSelections() {
        window.textBox("makeField").requireEmpty();
        window.textBox("modelField").requireEmpty();
        window.textBox("priceField").requireEmpty();
        window.textBox("speedField").requireEmpty();
        window.textBox("consumptionField").requireEmpty();
        window.comboBox("typeComboBox").requireSelection("Бензин/Дизель");
        window.comboBox("fuelTypeComboBox").requireSelection("Бензин");
        window.comboBox("fuelTypeComboBox").requireVisible();
        window.label("consumptionLabel").requireText("Витрата (л/100км):");
    }

    @Test
    public void addGasCar_whenValidData_shouldAddCarAndCloseDialog() {
        window.textBox("makeField").enterText("Toyota");
        window.textBox("modelField").enterText("Camry");
        window.comboBox("typeComboBox").selectItem("Бензин/Дизель");
        window.comboBox("fuelTypeComboBox").selectItem("Бензин");
        window.textBox("priceField").enterText("25000");
        window.textBox("speedField").enterText("220");
        window.textBox("consumptionField").enterText("8.5");

        window.button("addButton").click();

        // Перевіряємо, що діалог закрився (сам екземпляр діалогу)
        assertThat(dialog.isVisible()).isFalse();

        // Перевіряємо, що машина додана до автопарку
        List<Car> cars = taxiFleet.getCars();
        assertThat(cars).hasSize(1);
        Car addedCar = cars.get(0);
        assertThat(addedCar).isInstanceOf(GasCar.class);
        assertThat(addedCar.getMake()).isEqualTo("Toyota");
        assertThat(addedCar.getModel()).isEqualTo("Camry");
        // ... інші перевірки властивостей машини

        // Перевіряємо, що CarListPanel був оновлений
        verify(mockCarListPanel).loadAndSortCars();

        // Перевіряємо повідомлення про успіх
        // JOptionPaneFinder.findOptionPane() шукає будь-який видимий JOptionPane
        JOptionPaneFixture successPane = JOptionPaneFinder.findOptionPane().using(robot);
        successPane.requireMessage("Авто Toyota Camry успішно додано до автопарку!")
                .requireTitle("Успішне додавання")
                .okButton().click();
    }

    @Test
    public void addElectricCar_whenValidData_shouldAddCarAndCloseDialog() {
        window.textBox("makeField").enterText("Tesla");
        window.textBox("modelField").enterText("Model S");
        window.comboBox("typeComboBox").selectItem("Електричний");
        // fuelTypeComboBox має бути невидимим
        window.textBox("priceField").enterText("80000");
        window.textBox("speedField").enterText("250");
        window.textBox("consumptionField").enterText("15.0");

        window.button("addButton").click();

        assertThat(dialog.isVisible()).isFalse();

        List<Car> cars = taxiFleet.getCars();
        assertThat(cars).hasSize(1);
        Car addedCar = cars.get(0);
        assertThat(addedCar).isInstanceOf(ElectricCar.class);
        assertThat(addedCar.getMake()).isEqualTo("Tesla");
        // ... інші перевірки

        verify(mockCarListPanel).loadAndSortCars();

        JOptionPaneFixture successPane = JOptionPaneFinder.findOptionPane().using(robot);
        successPane.requireMessage("Авто Tesla Model S успішно додано до автопарку!")
                .requireTitle("Успішне додавання")
                .okButton().click();
    }

//    @Test
//    public void typeComboBox_whenChangedToElectric_shouldHideFuelTypeAndChangeConsumptionLabel() {
//        window.comboBox("typeComboBox").selectItem("Електричний");
//        window.comboBox("fuelTypeComboBox").requireNotVisible();
//        window.label("consumptionLabel").requireText("Витрата (кВт·год/100км):");
//    }

    @Test
    public void typeComboBox_whenChangedBackToGas_shouldShowFuelTypeAndChangeConsumptionLabel() {
        // Спочатку на електричний
        window.comboBox("typeComboBox").selectItem("Електричний");
        // Потім назад
        window.comboBox("typeComboBox").selectItem("Бензин/Дизель");
        window.comboBox("fuelTypeComboBox").requireVisible();
        window.label("consumptionLabel").requireText("Витрата (л/100км):");
    }

    @Test
    public void addButton_whenMakeIsEmpty_shouldShowWarningAndNotCloseDialog() {
        // Залишаємо makeField порожнім
        window.textBox("modelField").enterText("Model Y");
        window.textBox("priceField").enterText("60000");
        window.textBox("speedField").enterText("230");
        window.textBox("consumptionField").enterText("16.0");
        window.comboBox("typeComboBox").selectItem("Електричний");

        window.button("addButton").click();

        // Діалог має залишитися видимим
        assertThat(dialog.isVisible()).isTrue(); // або window.requireVisible();

        // Перевіряємо JOptionPane, який належить діалогу
        JOptionPaneFixture optionPane = window.optionPane();
        optionPane.requireWarningMessage().requireMessage("Марка та модель не можуть бути порожніми");
        optionPane.okButton().click();

        // Перевіряємо, що нічого не додано
        assertThat(taxiFleet.getCars()).isEmpty();
        verify(mockCarListPanel, never()).loadAndSortCars(); // Перевірка, що метод не викликався
    }

    @Test
    public void addButton_whenPriceIsInvalid_shouldShowErrorAndNotCloseDialog() {
        window.textBox("makeField").enterText("Kia");
        window.textBox("modelField").enterText("Sportage");
        window.textBox("priceField").enterText("not-a-number"); // Некоректне значення
        window.textBox("speedField").enterText("190");
        window.textBox("consumptionField").enterText("7.5");

        window.button("addButton").click();

        assertThat(dialog.isVisible()).isTrue();

        JOptionPaneFixture optionPane = window.optionPane();
        optionPane.requireErrorMessage()
                .requireMessage("Будь ласка, введіть коректні числові значення для ціни, швидкості та витрати");
        optionPane.okButton().click();

        assertThat(taxiFleet.getCars()).isEmpty();
        verify(mockCarListPanel, never()).loadAndSortCars();
    }

    @Test
    public void clearButton_shouldClearAllFieldsAndResetComboBoxes() {
        // Заповнюємо поля
        window.textBox("makeField").enterText("BMW");
        window.textBox("modelField").enterText("X5");
        window.comboBox("typeComboBox").selectItem("Електричний"); // Змінюємо для перевірки скидання
        window.textBox("priceField").enterText("75000");
        window.textBox("speedField").enterText("240");
        window.textBox("consumptionField").enterText("20.0");

        window.button("clearButton").click();

        window.textBox("makeField").requireEmpty();
        window.textBox("modelField").requireEmpty();
        window.textBox("priceField").requireEmpty();
        window.textBox("speedField").requireEmpty();
        window.textBox("consumptionField").requireEmpty();
        window.comboBox("typeComboBox").requireSelection("Бензин/Дизель"); // Перевіряємо скидання
        window.comboBox("fuelTypeComboBox").requireVisible(); // Має знову стати видимим
        window.label("consumptionLabel").requireText("Витрата (л/100км):");
    }

    @Test
    public void cancelButton_shouldCloseDialog() {
        window.button("cancelButton").click();
        assertThat(dialog.isVisible()).isFalse();
    }
}