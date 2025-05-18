package gui.panel;

import models.TaxiFleet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FleetManagementPanelTest {

    private TaxiFleet fleet;
    private FleetManagementPanel panel;

    @BeforeEach
    void setUp() {
        fleet = new TaxiFleet("Test Fleet");
        panel = new FleetManagementPanel(fleet);
    }

    @Test
    void testCloseButtonCreation() {
        // Verify the close button exists and has correct text
        Component[] components = panel.getComponents();
        JButton closeButton = null;

        for (Component comp : components) {
            if (comp instanceof JButton && "Закрити таксопарк".equals(((JButton) comp).getText())) {
                closeButton = (JButton) comp;
                break;
            }
        }

        assertNotNull(closeButton, "Close button should exist");
        assertEquals("Закрити таксопарк", closeButton.getText(), "Button text should match");
    }

    @Test
    void testCloseButtonAction() {
        // Mock the parent JTabbedPane
        JTabbedPane mockParent = mock(JTabbedPane.class);

        // Use Mockito to mock static SwingUtilities method
        try (MockedStatic<SwingUtilities> utilities = Mockito.mockStatic(SwingUtilities.class)) {
            utilities.when(() -> SwingUtilities.getAncestorOfClass(JTabbedPane.class, panel))
                    .thenReturn(mockParent);

            // Find the close button
            JButton closeButton = findCloseButton();

            // Simulate button click
            closeButton.doClick();

            // Verify the parent tabbed pane was called to remove this panel
            verify(mockParent, times(1)).remove(panel);
        }
    }

    @Test
    void testCloseButtonActionWhenNoParent() {
        // Mock SwingUtilities to return null for parent
        try (MockedStatic<SwingUtilities> utilities = Mockito.mockStatic(SwingUtilities.class)) {
            utilities.when(() -> SwingUtilities.getAncestorOfClass(JTabbedPane.class, panel))
                    .thenReturn(null);

            // Find the close button
            JButton closeButton = findCloseButton();

            // Simulate button click - should not throw exceptions
            assertDoesNotThrow(() -> closeButton.doClick());
        }
    }

    @Test
    void testCloseButtonLayout() {
        // Verify the close button is in the SOUTH position of BorderLayout
        BorderLayout layout = (BorderLayout) panel.getLayout();
        Component southComponent = layout.getLayoutComponent(BorderLayout.SOUTH);

        assertNotNull(southComponent, "South component should exist");
        assertTrue(southComponent instanceof JButton, "South component should be a JButton");
        assertEquals("Закрити таксопарк", ((JButton) southComponent).getText(),
                "South component should be the close button");
    }

    private JButton findCloseButton() {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JButton && "Закрити таксопарк".equals(((JButton) comp).getText())) {
                return (JButton) comp;
            }
        }
        throw new AssertionError("Close button not found");
    }
}