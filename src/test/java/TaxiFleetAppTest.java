import Interface.EmailService;
import gui.MainFrame;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import utils.GlobalExceptionHandler;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaxiFleetAppTest {

    @Test
    public void testMainMethodRunsWithoutErrors() {
        try (MockedStatic<SwingUtilities> mockedSwing = Mockito.mockStatic(SwingUtilities.class);
             MockedConstruction<MainFrame> mockedMainFrame = Mockito.mockConstruction(MainFrame.class)) {

            mockedSwing.when(() -> SwingUtilities.invokeLater(any(Runnable.class))).thenAnswer(invocation -> {
                Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            });

            TaxiFleetApp.main(new String[]{});

            // Verify MainFrame was constructed
            assertEquals(1, mockedMainFrame.constructed().size());
        }
    }

    @Test
    public void testGlobalExceptionHandlerSetup() {
        try (MockedStatic<SwingUtilities> mockedSwing = Mockito.mockStatic(SwingUtilities.class)) {
            mockedSwing.when(() -> SwingUtilities.invokeLater(any(Runnable.class))).thenAnswer(invocation -> null);

            Thread.UncaughtExceptionHandler initialHandler = Thread.getDefaultUncaughtExceptionHandler();
            try {
                TaxiFleetApp.main(new String[]{});
                Thread.UncaughtExceptionHandler newHandler = Thread.getDefaultUncaughtExceptionHandler();
                assertNotNull(newHandler, "UncaughtExceptionHandler should be set");
            } finally {
                Thread.setDefaultUncaughtExceptionHandler(initialHandler);
            }
        }
    }

    @Test
    public void testMainFrameInitialization() {
        try (MockedStatic<SwingUtilities> mockedSwing = Mockito.mockStatic(SwingUtilities.class);
             MockedConstruction<MainFrame> mockedMainFrame = Mockito.mockConstruction(MainFrame.class,
                     (mock, context) -> {
                         when(mock.isVisible()).thenReturn(true);
                     })) {

            mockedSwing.when(() -> SwingUtilities.invokeLater(any(Runnable.class))).thenAnswer(invocation -> {
                Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            });

            TaxiFleetApp.main(new String[]{});

            // Verify MainFrame was made visible
            verify(mockedMainFrame.constructed().get(0)).setVisible(true);
        }
    }

    @Test
    public void testErrorHandlingLogic() {
        // Мок для EmailService
        EmailService mockEmailService = mock(EmailService.class);
        when(mockEmailService.sendErrorEmail(anyString(), anyString())).thenReturn(true);

        // Тестовий виняток
        Exception testException = new RuntimeException("Test error");

        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class);
             MockedStatic<SwingUtilities> mockedSwing = Mockito.mockStatic(SwingUtilities.class)) {

            // Налаштовуємо мок для SwingUtilities.invokeLater
            mockedSwing.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
                    .thenAnswer(invocation -> {
                        Runnable runnable = invocation.getArgument(0);
                        runnable.run(); // Виконуємо Runnable одразу
                        return null;
                    });

            // Налаштовуємо мок для JOptionPane
            mockedOptionPane.when(() -> JOptionPane.showMessageDialog(
                    any(), anyString(), anyString(), anyInt()
            )).thenAnswer(invocation -> null);

            // Викликаємо обробник
            Thread.UncaughtExceptionHandler handler = new GlobalExceptionHandler(mockEmailService);
            handler.uncaughtException(Thread.currentThread(), testException);

            // Перевіряємо виклик JOptionPane з правильними аргументами
            mockedOptionPane.verify(() -> JOptionPane.showMessageDialog(
                    eq(null),
                    eq("Сталася критична помилка. Деталі були записані в лог та відправлені адміністратору."),
                    eq("Критична помилка"),
                    eq(JOptionPane.ERROR_MESSAGE)
            ));

            // Перевіряємо відправку email з більш гнучкими умовами
            verify(mockEmailService).sendErrorEmail(
                    anyString(), // Дозволяємо будь-який заголовок
                    contains("Test error") // Перевіряємо, що текст містить повідомлення про помилку
            );
        }
    }
}