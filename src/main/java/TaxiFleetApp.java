import Interface.EmailService;
import gui.MainFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.EmailSender;
import utils.GlobalExceptionHandler;

import javax.swing.*;

/**
 * Головний клас додатку Taxi Fleet.
 * Відповідає за запуск додатку, початкову ініціалізацію та обробку помилок.
 */
public class TaxiFleetApp {
    // Logger для запису подій додатку
    private static final Logger logger = LogManager.getLogger(TaxiFleetApp.class);

    /**
     * Головний метод програми.
     * Встановлює глобальний обробник винятків, запускає головне вікно додатку
     * та налаштовує основні компоненти.
     *
     * @param args аргументи командного рядка (не використовуються)
     */
    public static void main(String[] args) {
        // Створюємо інстанс email-сервісу
        EmailService emailService = new EmailSender();

        // Встановлення глобального обробника некоректно оброблених винятків
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler(emailService));
        logger.info("Starting Taxi Fleet Application");

        // Запуск графічного інтерфейсу в окремому потоці обробки подій Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Тестова помилка для перевірки обробки винятків
                // int test = 1 / 0;

                // Створення та відображення головного вікна додатку
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                logger.info("Application started successfully");

            } catch (Exception e) {
                // Обробка помилок, які трапились під час запуску додатку
                logger.error("Failed to start application", e);

                // Відправлення сповіщення про помилку електронною поштою
                boolean sent = emailService.sendErrorEmail(
                        "Critical Error in TaxiFleetApp",
                        "An unexpected error occurred:\n" + e.toString()
                );

                if (!sent) {
                    logger.warn("Failed to notify admin via email");
                }

                // Відображення повідомлення про помилку користувачу
                JOptionPane.showMessageDialog(
                        null,
                        "Помилка запуску програми: " + e.getMessage(),
                        "Критична помилка",
                        JOptionPane.ERROR_MESSAGE
                );

                // Завершення роботи додатку з кодом помилки
                System.exit(1);
            }
        });
    }
}