package utils;

import Interface.EmailService;
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

    private final EmailService emailService;

    public GlobalExceptionHandler(EmailService emailService) {
        this.emailService = emailService;
    }
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
        try {
            logger.fatal("Unhandled exception in thread: " + t.getName(), e);

            try {
                emailService.sendErrorEmail(
                        "Taxi Fleet Critical Error",
                        "Unhandled exception in thread: " + t.getName() + "\n" +
                                "Error: " + e.toString() + "\n" +
                                "Stack trace: " + getStackTraceAsString(e)
                );
            } catch (Exception emailEx) {
                logger.error("Failed to send error email", emailEx); // Цей рядок має викликатися
            }
        } catch (Exception loggingEx) {
            System.err.println("Critical logging failure: " + loggingEx.getMessage());
        }

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
    String getStackTraceAsString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        // Додаємо інформацію про основний виняток
        appendExceptionInfo(sb, e);

        // Додаємо причину винятку (cause), якщо вона є
        Throwable cause = e.getCause();
        if (cause != null) {
            sb.append("\nCaused by: ");
            appendExceptionInfo(sb, cause);
        }

        return sb.toString();
    }

    private void appendExceptionInfo(StringBuilder sb, Throwable e) {
        sb.append(e.getClass().getName()).append(": ").append(e.getMessage()).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
    }
}