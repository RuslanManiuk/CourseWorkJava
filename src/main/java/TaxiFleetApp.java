import gui.MainFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.GlobalExceptionHandler;

import javax.swing.*;

/**
 * Головний клас програми Taxi Fleet.
 * Відповідає за ініціалізацію програми, налаштування логування та
 * обробки помилок, а також запуск головного вікна інтерфейсу.
 */
public class TaxiFleetApp {

    // Логгер для запису інформації про роботу програми
    private static final Logger logger = LogManager.getLogger(TaxiFleetApp.class);

    /**
     * Головний метод програми, точка входу.
     * Налаштовує глобальний обробник винятків та запускає головне вікно програми.
     *
     * @param args аргументи командного рядка (не використовуються)
     */
    public static void main(String[] args) {
        // Встановлюємо глобальний обробник винятків
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());

        // Записуємо в лог інформацію про запуск програми
        logger.info("Starting Taxi Fleet Application");

        // Створюємо та відображаємо головне вікно в UI потоці
        SwingUtilities.invokeLater(() -> {
            try {
                // Ініціалізація та відображення головного вікна
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);

                // Записуємо в лог успішний запуск
                logger.info("Application started successfully");
            } catch (Exception e) {
                // Обробляємо помилки ініціалізації
                logger.fatal("Failed to start application", e);

                // Показуємо користувачу повідомлення про помилку
                JOptionPane.showMessageDialog(
                        null,
                        "Помилка запуску програми: " + e.getMessage(),
                        "Критична помилка",
                        JOptionPane.ERROR_MESSAGE
                );

                // Завершуємо роботу програми з кодом помилки
                System.exit(1);
            }
        });
    }
}