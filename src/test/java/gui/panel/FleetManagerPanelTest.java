package gui.panel;

import models.TaxiFleet;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.core.matcher.JButtonMatcher; // Імпорт для матчера
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FleetManagementPanelTest {

    private TaxiFleet fleet;
    private FleetManagementPanel panelInstance; // Зберігаємо екземпляр панелі
    private JPanelFixture panelFixture; // Fixture для взаємодії з панеллю
    private Robot robot;

    // Метод для запуску один раз перед усіма тестами
    @BeforeAll
    static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install(); // Дуже важливо!
    }

    @BeforeEach
    void setUp() {
        robot = BasicRobot.robotWithCurrentAwtHierarchy();
        robot.settings().delayBetweenEvents(50); // Опціонально, для стабільності

        fleet = new TaxiFleet("Test Fleet");

        // Створюємо панель в EDT
        // Для коректної роботи getAncestorOfClass, панель повинна бути в ієрархії компонентів,
        // тому ми додаємо її до тимчасового JFrame.
        JFrame testFrame = GuiActionRunner.execute(() -> {
            // Створюємо панель всередині GuiActionRunner, щоб її конструктор викликався в EDT
            panelInstance = new FleetManagementPanel(fleet);
            // Рекомендую встановити ім'я кнопці в FleetManagementPanel:
            // наприклад, closeButton.setName("closeFleetButton");

            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Важливо для очищення
            frame.add(panelInstance);
            frame.pack();
            frame.setVisible(true);
            return frame;
        });

        // Створюємо fixture для панелі
        panelFixture = new JPanelFixture(robot, panelInstance);
        // Зберігаємо фрейм, щоб потім його закрити
        panelFixture.target().putClientProperty("testFrame", testFrame);
    }

    @AfterEach
    void tearDown() {
        // Закриваємо тестовий фрейм
        JFrame testFrame = (JFrame) panelFixture.target().getClientProperty("testFrame");
        if (testFrame != null) {
            GuiActionRunner.execute(testFrame::dispose);
        }
        robot.cleanUp();
    }

    private JButtonFixture findCloseButtonFixture() {
        // Варіант 1: Пошук за текстом (якщо ім'я не встановлено)
        return panelFixture.button(JButtonMatcher.withText("Закрити таксопарк"));
        // Варіант 2: Пошук за іменем (рекомендовано, якщо ім'я встановлено в FleetManagementPanel)
        // return panelFixture.button("closeFleetButton");
    }

    @Test
    void testCloseButtonCreation() {
        JButtonFixture closeButton = findCloseButtonFixture();
        closeButton.requireVisible();
        assertEquals("Закрити таксопарк", closeButton.target().getText());
    }

    @Test
    void testCloseButtonLayout() {
        // Отримуємо layout панелі (це безпечно робити не в EDT, бо ми читаємо стан вже створеного об'єкта)
        BorderLayout layout = (BorderLayout) panelInstance.getLayout();

        Component southComponent = layout.getLayoutComponent(BorderLayout.SOUTH);

        assertNotNull(southComponent, "South component should exist");
        assertTrue(southComponent instanceof JButton, "South component should be a JButton");
        assertEquals("Закрити таксопарк", ((JButton) southComponent).getText(),
                "South component should be the close button");

        // Перевірка, що це саме та кнопка, яку ми очікуємо
        JButtonFixture closeButtonFixture = findCloseButtonFixture();
        assertSame(southComponent, closeButtonFixture.target(), "The JButton in BorderLayout.SOUTH should be the close button");
    }
}