package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Глобальний обробник некоректно оброблених винятків в додатку.
 * Відповідає за логування, відправлення повідомлень про помилки та
 * відображення інформації користувачу при виникненні некоректно оброблених помилок.
 */
public class GlobalExceptionHandler implements UncaughtExceptionHandler {
    // Logger для запису помилок
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * Обробляє некоректно оброблені винятки в потоках.
     * Записує інформацію про виняток у лог, відправляє сповіщення електронною поштою
     * та відображає повідомлення користувачу.
     *
     * @param t потік, в якому виник виняток
     * @param e виняток, що виник
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.fatal("Unhandled exception in thread: " + t.getName(), e);

        EmailSender.sendErrorEmail(
                "Taxi Fleet Critical Error",
                "Unhandled exception in thread: " + t.getName() + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "Stack trace: " + getStackTraceAsString(e)
        );

        // Показати повідомлення користувачу
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
     * Перетворює стек виклику винятку в рядок для зручного відображення.
     *
     * @param e виняток, стек виклику якого потрібно отримати
     * @return рядок, що містить інформацію про стек виклику
     */
    private String getStackTraceAsString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}