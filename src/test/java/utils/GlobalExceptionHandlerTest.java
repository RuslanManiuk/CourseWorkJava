package utils;

import Interface.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.mockito.ArgumentCaptor;
import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    @Test
    void testGetStackTraceAsString() {
        EmailService mockEmail = mock(EmailService.class);
        GlobalExceptionHandler handler = new GlobalExceptionHandler(mockEmail);

        // Тест звичайного винятку
        Exception testEx = new RuntimeException("Test message");
        String result = handler.getStackTraceAsString(testEx);
        assertNotNull(result);
        assertTrue(result.contains("RuntimeException"));
        assertTrue(result.contains("Test message"));
        assertTrue(result.contains("\tat "));

        // Тест винятку з причиною (cause)
        Exception cause = new IllegalArgumentException("Root cause");
        Exception nestedEx = new RuntimeException("Wrapper", cause);
        String nestedResult = handler.getStackTraceAsString(nestedEx);
        assertNotNull(nestedResult);
        assertTrue(nestedResult.contains("RuntimeException"));
        assertTrue(nestedResult.contains("Wrapper"));
        assertTrue(nestedResult.contains("Caused by:"));
        assertTrue(nestedResult.contains("IllegalArgumentException"));
        assertTrue(nestedResult.contains("Root cause"));
    }

    @Test
    void testUncaughtException() throws Exception {
        EmailService mockEmail = mock(EmailService.class);
        Logger mockLogger = mock(Logger.class);

        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class);
             MockedStatic<SwingUtilities> mockedSwing = mockStatic(SwingUtilities.class);
             MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {

            mockedLogManager.when(() -> LogManager.getLogger(GlobalExceptionHandler.class))
                    .thenReturn(mockLogger);

            mockedSwing.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
                    .thenAnswer(invocation -> {
                        invocation.getArgument(0, Runnable.class).run();
                        return null;
                    });

            GlobalExceptionHandler handler = new GlobalExceptionHandler(mockEmail);
            Thread testThread = new Thread("TestThread");
            Exception testEx = new RuntimeException("Test exception");

            handler.uncaughtException(testThread, testEx);

            // Перевіряємо логування
            verify(mockLogger).fatal(eq("Unhandled exception in thread: TestThread"), eq(testEx));

            // Перевіряємо email
            ArgumentCaptor<String> emailContentCaptor = ArgumentCaptor.forClass(String.class);
            verify(mockEmail).sendErrorEmail(
                    eq("Taxi Fleet Critical Error"),
                    emailContentCaptor.capture()
            );

            String emailContent = emailContentCaptor.getValue();
            assertTrue(emailContent.contains("Unhandled exception in thread: TestThread"));
            assertTrue(emailContent.contains("RuntimeException"));
            assertTrue(emailContent.contains("Test exception"));
            assertTrue(emailContent.contains("Stack trace:"));

            // Перевіряємо діалогове вікно
            mockedPane.verify(() ->
                    JOptionPane.showMessageDialog(
                            null,
                            "Сталася критична помилка. Деталі були записані в лог та відправлені адміністратору.",
                            "Критична помилка",
                            JOptionPane.ERROR_MESSAGE
                    )
            );
        }
    }

    @Test
    void testUncaughtException_EmailFailure() throws Exception {
        // Arrange
        EmailService mockEmail = mock(EmailService.class);
        Logger mockLogger = mock(Logger.class);

        // Налаштовуємо мок для викидання помилки при відправці email
        doThrow(new RuntimeException("Email send failed"))
                .when(mockEmail).sendErrorEmail(anyString(), anyString());

        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> LogManager.getLogger(GlobalExceptionHandler.class))
                    .thenReturn(mockLogger);

            GlobalExceptionHandler handler = new GlobalExceptionHandler(mockEmail);
            Thread testThread = new Thread("TestThread");
            Exception testEx = new RuntimeException("Test exception");

            // Act
            handler.uncaughtException(testThread, testEx);

            // Assert
            // Перевіряємо, що основний виняток був залогований
            verify(mockLogger).fatal(eq("Unhandled exception in thread: TestThread"), eq(testEx));

            // Перевіряємо, що помилка email була залогована
            ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);
            verify(mockLogger).error(
                    eq("Failed to send error email"),
                    exceptionCaptor.capture()
            );

            // Додаткова перевірка захопленого винятку
            Exception loggedEx = exceptionCaptor.getValue();
            assertNotNull(loggedEx);
            assertEquals("Email send failed", loggedEx.getMessage());
        }
    }
}