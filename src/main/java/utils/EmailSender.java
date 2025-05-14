package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
    private static final Logger logger = LogManager.getLogger(EmailSender.class);
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String USERNAME = "o.1234567890.n.1234567890.e.1234567890@gmail.com";
    private static final String PASSWORD = "one1one1";
    private static final String FROM_EMAIL = "o.1234567890.n.1234567890.e.1234567890@gmail.com";
    private static final String TO_EMAIL = "ruslanmanjuk@gmail.com";

    public static void sendErrorEmail(String subject, String message) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });

        try {
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(FROM_EMAIL));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO_EMAIL));
            emailMessage.setSubject(subject);
            emailMessage.setText(message);

            Transport.send(emailMessage);
            logger.info("Critical error email sent successfully");
        } catch (MessagingException e) {
            logger.error("Failed to send error email", e);
        }
    }
}