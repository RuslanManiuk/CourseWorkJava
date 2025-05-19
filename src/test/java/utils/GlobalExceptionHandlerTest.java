package utils;

import Interface.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private Logger logger;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        // Використовуємо конструктор з логером для тестування
        exceptionHandler = new GlobalExceptionHandler(emailService, logger);
    }

    @Test
    void testGetStackTraceAsString() {
        EmailService mockEmail = mock(EmailService.class);
        GlobalExceptionHandler handler = new GlobalExceptionHandler(mockEmail);

        Exception testEx = new RuntimeException("Test message");
        String result = handler.getStackTraceAsString(testEx);
        assertNotNull(result);
        assertTrue(result.contains("RuntimeException"));
        assertTrue(result.contains("Test message"));
        assertTrue(result.contains("\tat "));

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
    void testUncaughtException() {
        try (MockedStatic<SwingUtilities> mockedSwing = mockStatic(SwingUtilities.class);
             MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {

            mockedSwing.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
                    .thenAnswer(invocation -> {
                        invocation.getArgument(0, Runnable.class).run();
                        return null;
                    });

            Thread testThread = new Thread("TestThread");
            Exception testEx = new RuntimeException("Test exception");

            exceptionHandler.uncaughtException(testThread, testEx);

            // Тепер ця перевірка має працювати
            verify(logger).fatal(eq("Unhandled exception in thread: TestThread"), eq(testEx));
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
    void testUncaughtException_EmailFailure() {
        EmailService mockEmail = mock(EmailService.class);
        Logger mockLogger = mock(Logger.class);

        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> LogManager.getLogger(GlobalExceptionHandler.class))
                    .thenReturn(mockLogger);

            GlobalExceptionHandler handler = new GlobalExceptionHandler(mockEmail);
            Thread testThread = new Thread("TestThread");
            Exception testEx = new RuntimeException("Test exception");
            Exception emailEx = new RuntimeException("Email send failed");

            doThrow(emailEx).when(mockEmail).sendErrorEmail(anyString(), anyString());

            handler.uncaughtException(testThread, testEx);

            verify(mockLogger).fatal(eq("Unhandled exception in thread: TestThread"), eq(testEx));
            verify(mockEmail).sendErrorEmail(anyString(), anyString());
            verify(mockLogger).error(eq("Failed to send error email"), eq(emailEx));
        }
    }

    @Test
    void testUncaughtException_LoggingFailure() {
        EmailService mockEmail = mock(EmailService.class);
        Logger mockLogger = mock(Logger.class);

        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> LogManager.getLogger(GlobalExceptionHandler.class))
                    .thenReturn(mockLogger);

            GlobalExceptionHandler handler = new GlobalExceptionHandler(mockEmail);
            Thread testThread = new Thread("TestThread");
            Exception testEx = new RuntimeException("Test exception");

            doThrow(new RuntimeException("Logging failed")).when(mockLogger).fatal(anyString(), any(Throwable.class));

            handler.uncaughtException(testThread, testEx);

            verify(mockLogger).fatal(eq("Unhandled exception in thread: TestThread"), eq(testEx));
            verify(mockEmail, never()).sendErrorEmail(anyString(), anyString());
        }
    }
}