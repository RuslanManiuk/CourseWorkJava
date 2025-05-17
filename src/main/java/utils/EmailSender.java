package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Утиліта для відправки повідомлень електронної пошти.
 * Використовується для сповіщення про критичні помилки в системі.
 */
public class EmailSender {

    // Константи для логгера та налаштувань SMTP
    private static final Logger logger = LogManager.getLogger(EmailSender.class);

    // Налаштування SMTP-сервера
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    // Облікові дані для автентифікації
    private static final String USERNAME = "o.1234567890.n.1234567890.e.1234567890@gmail.com";
    private static final String PASSWORD = "one1one1";

    // Налаштування електронної пошти
    private static final String FROM_EMAIL = "o.1234567890.n.1234567890.e.1234567890@gmail.com";
    private static final String TO_EMAIL = "ruslanmanjuk@gmail.com";

    /**
     * Надсилає електронний лист зі сповіщенням про помилку.
     *
     * @param subject заголовок листа
     * @param message текст повідомлення з описом помилки
     */
    public static void sendErrorEmail(String subject, String message) {
        // Налаштування властивостей підключення
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        // Створення сесії з автентифікацією
        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });

        try {
            // Створення та налаштування повідомлення
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(FROM_EMAIL));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO_EMAIL));
            emailMessage.setSubject(subject);
            emailMessage.setText(message);

            // Відправка повідомлення
            Transport.send(emailMessage);
            logger.info("Critical error email sent successfully");
        } catch (MessagingException e) {
            logger.error("Failed to send error email", e);
        }
    }
}