package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Глобальний обробник необроблених винятків у програмі.
 * Відповідає за логування винятків, відправку сповіщень електронною поштою
 * та відображення повідомлення користувачу.
 */
public class GlobalExceptionHandler implements UncaughtExceptionHandler {

    // Логгер для запису інформації про виняткові ситуації
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * Обробляє необроблені виключення, що виникають у будь-якому потоці програми.
     * Записує інформацію про виняток у лог, надсилає сповіщення електронною поштою
     * та показує діалогове вікно користувачу.
     *
     * @param t потік, у якому виникло виключення
     * @param e виняток, який виник
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // Записуємо критичну помилку в лог
        logger.fatal("Unhandled exception in thread: " + t.getName(), e);

        // Надсилаємо сповіщення про помилку електронною поштою
        EmailSender.sendErrorEmail(
                "Taxi Fleet Critical Error",
                "Unhandled exception in thread: " + t.getName() + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "Stack trace: " + getStackTraceAsString(e)
        );

        // Показуємо повідомлення користувачу в UI потоці
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    null,
                    "Сталася критична помилка. Деталі були записані в лог та відправлені адміністратору.",
                    "Критична помилка",
                    JOptionPane.ERROR_MESSAGE
            );
        });
    }

    /**
     * Перетворює стек-трейс виключення у рядок.
     *
     * @param e виняток, стек-трейс якого потрібно отримати
     * @return рядок, що містить форматований стек-трейс
     */
    private String getStackTraceAsString(Throwable e) {
        StringBuilder sb = new StringBuilder();

        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }

        return sb.toString();
    }
}