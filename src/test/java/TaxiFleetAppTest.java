//import Interface.EmailService;
//import gui.MainFrame;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockedStatic;
//import org.mockito.MockedConstruction;
//import utils.EmailSender;
//import utils.GlobalExceptionHandler;
//
//import javax.swing.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//class TaxiFleetAppTest {
//
//    @Test
//    void testMain_SuccessfulStartup() {
//        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class);
//             MockedStatic<SwingUtilities> mockedSwing = mockStatic(SwingUtilities.class);
//             MockedConstruction<EmailSender> mockedEmail = mockConstruction(EmailSender.class);
//             MockedConstruction<GlobalExceptionHandler> mockedHandler = mockConstruction(GlobalExceptionHandler.class);
//             MockedConstruction<MainFrame> mockedFrame = mockConstruction(MainFrame.class)) {
//
//            // Arrange
//            Logger mockLogger = mock(Logger.class);
//            mockedLogManager.when(() -> LogManager.getLogger(any(Class.class)))
//                    .thenReturn(mockLogger);
//
//            mockedSwing.when(SwingUtilities::isEventDispatchThread).thenReturn(false);
//            mockedSwing.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
//                    .thenAnswer(invocation -> {
//                        invocation.getArgument(0, Runnable.class).run();
//                        return null;
//                    });
//
//            // Act
//            assertDoesNotThrow(() -> TaxiFleetApp.main(new String[]{}));
//
//            // Assert
//            verify(mockLogger).info("Starting Taxi Fleet Application");
//            verify(mockLogger).info("Application started successfully");
//            assertEquals(1, mockedHandler.constructed().size());
//            assertEquals(1, mockedFrame.constructed().size());
//            verify(mockedFrame.constructed().get(0)).setVisible(true);
//        }
//    }
//
//    @Test
//    void testMain_StartupFailure() {
//        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class);
//             MockedStatic<SwingUtilities> mockedSwing = mockStatic(SwingUtilities.class);
//             MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class);
//             MockedConstruction<EmailSender> mockedEmail = mockConstruction(EmailSender.class, (mock, context) -> {
//                 when(mock.sendErrorEmail(anyString(), anyString())).thenReturn(true);
//             });
//             MockedConstruction<GlobalExceptionHandler> mockedHandler = mockConstruction(GlobalExceptionHandler.class);
//             MockedConstruction<MainFrame> mockedFrame = mockConstruction(MainFrame.class, (mock, context) -> {
//                 throw new RuntimeException("Test startup error");
//             })) {
//
//            // Arrange
//            Logger mockLogger = mock(Logger.class);
//            mockedLogManager.when(() -> LogManager.getLogger(any(Class.class)))
//                    .thenReturn(mockLogger);
//
//            mockedSwing.when(SwingUtilities::isEventDispatchThread).thenReturn(false);
//            mockedSwing.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
//                    .thenAnswer(invocation -> {
//                        invocation.getArgument(0, Runnable.class).run();
//                        return null;
//                    });
//
//            // Act & Assert
//            assertDoesNotThrow(() -> TaxiFleetApp.main(new String[]{}));
//
//            // Verify
//            verify(mockLogger).error(eq("Failed to start application"), any(RuntimeException.class));
//            verify(mockedEmail.constructed().get(0)).sendErrorEmail(
//                    eq("Critical Error in TaxiFleetApp"),
//                    contains("Test startup error"));
//            mockedPane.verify(() -> JOptionPane.showMessageDialog(
//                    isNull(),
//                    contains("Помилка запуску програми"),
//                    eq("Критична помилка"),
//                    eq(JOptionPane.ERROR_MESSAGE)));
//        }
//    }
//
//    @Test
//    void testMain_EmailSendFailure() {
//        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class);
//             MockedStatic<SwingUtilities> mockedSwing = mockStatic(SwingUtilities.class);
//             MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class);
//             MockedConstruction<EmailSender> mockedEmail = mockConstruction(EmailSender.class, (mock, context) -> {
//                 when(mock.sendErrorEmail(anyString(), anyString())).thenReturn(false);
//             });
//             MockedConstruction<GlobalExceptionHandler> mockedHandler = mockConstruction(GlobalExceptionHandler.class);
//             MockedConstruction<MainFrame> mockedFrame = mockConstruction(MainFrame.class, (mock, context) -> {
//                 throw new RuntimeException("Test startup error");
//             })) {
//
//            // Arrange
//            Logger mockLogger = mock(Logger.class);
//            mockedLogManager.when(() -> LogManager.getLogger(any(Class.class)))
//                    .thenReturn(mockLogger);
//
//            mockedSwing.when(SwingUtilities::isEventDispatchThread).thenReturn(false);
//            mockedSwing.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
//                    .thenAnswer(invocation -> {
//                        invocation.getArgument(0, Runnable.class).run();
//                        return null;
//                    });
//
//            // Act & Assert
//            assertDoesNotThrow(() -> TaxiFleetApp.main(new String[]{}));
//
//            // Verify
//            verify(mockLogger).warn("Failed to notify admin via email");
//            verify(mockLogger).error(eq("Failed to start application"), any(RuntimeException.class));
//            mockedPane.verify(() -> JOptionPane.showMessageDialog(
//                    isNull(),
//                    contains("Помилка запуску програми"),
//                    eq("Критична помилка"),
//                    eq(JOptionPane.ERROR_MESSAGE)));
//        }
//    }
//}


import models.cars.ElectricCar;
import models.cars.GasCar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
