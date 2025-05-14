import gui.MainFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.GlobalExceptionHandler;

import javax.swing.*;

public class TaxiFleetApp {
    private static final Logger logger = LogManager.getLogger(TaxiFleetApp.class);

    public static void main(String[] args) {
        // Встановлюємо глобальний обробник винятків
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());

        logger.info("Starting Taxi Fleet Application");
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                logger.info("Application started successfully");
            } catch (Exception e) {
                logger.fatal("Failed to start application", e);
                JOptionPane.showMessageDialog(null,
                        "Помилка запуску програми: " + e.getMessage(),
                        "Критична помилка",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}