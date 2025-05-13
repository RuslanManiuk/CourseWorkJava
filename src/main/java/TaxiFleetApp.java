
import gui.MainFrame;

import javax.swing.*;

public class TaxiFleetApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}