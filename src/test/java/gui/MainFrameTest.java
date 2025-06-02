package gui;

import managers.TaxiFleetManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainFrameTest {

    @Mock
    private TaxiFleetManager mockFleetManager;

    private MainFrame mainFrame;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mainFrame = new MainFrame();
        mainFrame.setVisible(true); // Додаємо фрейм на екран перед тестами
    }

    @AfterEach
    void tearDown() {
        mainFrame.dispose(); // Закриваємо фрейм після тестів
    }

    @Test
    void testMainFrameInitialization() {
        assertNotNull(mainFrame);
        assertEquals("Управління таксопарками", mainFrame.getTitle());
        assertEquals(JFrame.EXIT_ON_CLOSE, mainFrame.getDefaultCloseOperation());
    }

    @Test
    void testMainFrameComponents() {
        JTabbedPane tabbedPane = (JTabbedPane) mainFrame.getContentPane().getComponent(0);
        assertNotNull(tabbedPane);
        assertEquals(1, tabbedPane.getTabCount());
        assertEquals("Таксопарки", tabbedPane.getTitleAt(0));
    }

    @Test
    void testMainFrameSize() {
        assertEquals(1400, mainFrame.getWidth());
        assertEquals(700, mainFrame.getHeight());
    }

    @Test
    void testMainFrameLocation() {
        assertDoesNotThrow(() -> {
            // Тепер цей тест не викличе помилку, оскільки фрейм відображається
            assertNotNull(mainFrame.getLocationOnScreen());
        });
    }
}