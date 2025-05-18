package Interface;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @Test
    public void testSendErrorEmail_SuccessCase() {
        // Створюємо mock реалізацію інтерфейсу
        EmailService emailService = mock(EmailService.class);

        // Налаштовуємо поведінку mock-об'єкта
        when(emailService.sendErrorEmail("Test Subject", "Test Message"))
                .thenReturn(true);

        // Виконуємо тест
        boolean result = emailService.sendErrorEmail("Test Subject", "Test Message");

        // Перевіряємо результати
        assertTrue(result);
        verify(emailService).sendErrorEmail("Test Subject", "Test Message");
    }

    @Test
    public void testSendErrorEmail_FailureCase() {
        EmailService emailService = mock(EmailService.class);

        when(emailService.sendErrorEmail(anyString(), anyString()))
                .thenReturn(false);

        boolean result = emailService.sendErrorEmail("Any Subject", "Any Message");

        assertFalse(result);
    }

    @Test
    public void testInterfaceContract() {
        // Перевірка, що інтерфейс має очікуваний метод
        assertDoesNotThrow(() -> {
            EmailService service = new EmailService() {
                @Override
                public boolean sendErrorEmail(String subject, String message) {
                    return false;
                }
            };

            boolean result = service.sendErrorEmail("Test", "Test");
            // Можна додати додаткові перевірки поведінки
        });
    }
}