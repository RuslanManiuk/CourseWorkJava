package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.lang.Thread.UncaughtExceptionHandler;

public class GlobalExceptionHandler implements UncaughtExceptionHandler {
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.fatal("Unhandled exception in thread: " + t.getName(), e);
        EmailSender.sendErrorEmail("Taxi Fleet Critical Error",
                "Unhandled exception in thread: " + t.getName() + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "Stack trace: " + getStackTraceAsString(e));

        // Показати повідомлення користувачу
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                    "Сталася критична помилка. Деталі були записані в лог та відправлені адміністратору.",
                    "Критична помилка",
                    JOptionPane.ERROR_MESSAGE);
        });
    }

    private String getStackTraceAsString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}