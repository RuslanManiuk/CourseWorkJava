package utils;

import Interface.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import utils.EmailSender;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailSenderTest {

    @Test
    void testSendErrorEmail_Success() {
        // мок static метод Transport.send(...)
        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
            EmailService emailService = new EmailSender(); // EmailSender реалізує EmailService
            boolean sent = emailService.sendErrorEmail("Subject", "Body");


            // перевіряємо, що Transport.send() було викликано
            mockedTransport.verify(() -> Transport.send(any(MimeMessage.class)), times(1));

            // перевірка, що метод повертає true
            assertTrue(sent);
        }
    }

    @Test
    void testSendErrorEmail_Failure() {
        // змушуємо Transport.send кинути виняток
        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
            mockedTransport.when(() -> Transport.send(any(MimeMessage.class)))
                    .thenThrow(new MessagingException("Simulated error"));

            EmailService emailService = new EmailSender();
            boolean result = emailService.sendErrorEmail("Error Subject", "Some error text");

            // перевірка, що метод обробляє виняток і повертає false
            assertFalse(result);
        }
    }
}
