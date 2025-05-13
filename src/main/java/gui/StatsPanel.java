package gui;

import models.TaxiFleet;
import javax.swing.*;
import java.awt.*;

public class StatsPanel extends JPanel {
    public StatsPanel(TaxiFleet taxiFleet) {
        setLayout(new BorderLayout());

        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);

        // Обчислення статистики
        long electricCount = taxiFleet.getCars().stream()
                .filter(c -> c.getFuelType().equals("Електричний"))
                .count();
        long gasCount = taxiFleet.getCars().size() - electricCount;
        double totalCost = taxiFleet.calculateTotalCost();
        double avgFuel = taxiFleet.calculateAverageFuelConsumption();

        // Формування тексту
        String statsText = String.format(
                "Статистика таксопарку '%s':\n\n" +
                        "Кількість авто: %d\n" +
                        "- Електричні: %d\n" +
                        "- Бензин/Дизель: %d\n\n" +
                        "Загальна вартість: %.2f $\n" +
                        "Середня витрата пального: %.2f %s",
                taxiFleet.getName(),
                taxiFleet.getCars().size(),
                electricCount,
                gasCount,
                totalCost,
                avgFuel,
                (electricCount > 0 ? "кВт·год/100км" : "л/100км")
        );

        statsArea.setText(statsText);
        add(new JScrollPane(statsArea), BorderLayout.CENTER);
    }
}