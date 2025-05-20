package gui.panel;

import models.TaxiFleet;
import models.TaxiFleetManager;
import gui.panel.StatsPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class FleetsManagementPanel extends JPanel {
    // Logger
    private static final Logger logger = LogManager.getLogger(FleetsManagementPanel.class);

    // Основні компоненти
    private TaxiFleetManager fleetManager;
    private JTabbedPane mainTabbedPane;
    private JList<TaxiFleet> fleetsList;
    private DefaultListModel<TaxiFleet> fleetsListModel;

    // Кольорова схема UI
    private static final Color PRIMARY_COLOR = new Color(52, 73, 94);
    static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(26, 188, 156);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color PANEL_COLOR = Color.WHITE;

    // Шрифти
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font LIST_FONT = new Font("Arial", Font.PLAIN, 14);

    /**
     * Конструктор панелі управління таксопарками
     */
    public FleetsManagementPanel(TaxiFleetManager fleetManager, JTabbedPane mainTabbedPane) {
        logger.info("Initializing FleetsManagementPanel");
        this.fleetManager = fleetManager;
        this.mainTabbedPane = mainTabbedPane;
        initUI();
    }

    /**
     * Ініціалізація користувацького інтерфейсу
     */
    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Верхня панель з заголовком та інформацією
        JPanel headerPanel = createHeaderPanel();

        // Створюємо основний контент
        JPanel contentPanel = new JPanel(new BorderLayout(15, 0));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Ліва панель зі списком та кнопками керування
        JPanel leftPanel = createLeftPanel();

        // Права панель з інформацією про вибраний таксопарк
        JPanel rightPanel = createRightPanel();

        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(rightPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Створення верхньої панелі з заголовком
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(getWidth(), 70));

        // Додаємо заголовок
        JLabel titleLabel = new JLabel("Управління таксопарками");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        // Додаємо панель з іконками та інформацією
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        iconPanel.setOpaque(false);

        // Можна додати статусні іконки або лічильники
        JLabel fleetCountLabel = new JLabel("Кількість таксопарків: " + fleetManager.getFleets().size());
        fleetCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        fleetCountLabel.setForeground(Color.WHITE);
        iconPanel.add(fleetCountLabel);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(iconPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Створення лівої панелі зі списком таксопарків
     */
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setPreferredSize(new Dimension(300, getHeight()));

        // Створюємо заголовок для панелі зі списком
        JPanel listHeaderPanel = new JPanel(new BorderLayout());
        listHeaderPanel.setBackground(SECONDARY_COLOR);
        listHeaderPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel listTitle = new JLabel("Доступні таксопарки");
        listTitle.setFont(HEADER_FONT);
        listTitle.setForeground(Color.WHITE);
        listHeaderPanel.add(listTitle, BorderLayout.WEST);

        // Створюємо модель для списку
        fleetsListModel = new DefaultListModel<>();
        updateFleetsList();

        // Створюємо список з кастомним рендерером
        fleetsList = new JList<>(fleetsListModel);
        fleetsList.setFont(LIST_FONT);
        fleetsList.setBackground(PANEL_COLOR);
        fleetsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fleetsList.setCellRenderer(new FleetListCellRenderer());
        fleetsList.setBorder(BorderFactory.createEmptyBorder());

        // Додаємо прокрутку
        JScrollPane scrollPane = new JScrollPane(fleetsList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        // Панель з кнопками керування
        JPanel buttonsPanel = createButtonsPanel();

        // Збираємо ліву панель
        panel.add(listHeaderPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Створення правої інформаційної панелі
     */
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Заглушка на випадок, коли жодного таксопарку не вибрано
        JPanel placeholderPanel = new JPanel();
        placeholderPanel.setLayout(new BoxLayout(placeholderPanel, BoxLayout.Y_AXIS));
        placeholderPanel.setBackground(PANEL_COLOR);

        // Додаємо зображення-заглушку
        JLabel imageLabel = new JLabel(createPlaceholderIcon(100, 100));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Додаємо текст-підказку
        JLabel hintLabel = new JLabel("Виберіть таксопарк зі списку зліва");
        hintLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        hintLabel.setForeground(TEXT_COLOR);
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel helpLabel = new JLabel("або створіть новий таксопарк за допомогою кнопки");
        helpLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        helpLabel.setForeground(new Color(149, 165, 166));
        helpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        placeholderPanel.add(Box.createVerticalGlue());
        placeholderPanel.add(imageLabel);
        placeholderPanel.add(Box.createVerticalStrut(20));
        placeholderPanel.add(hintLabel);
        placeholderPanel.add(Box.createVerticalStrut(10));
        placeholderPanel.add(helpLabel);
        placeholderPanel.add(Box.createVerticalGlue());

        panel.add(placeholderPanel, BorderLayout.CENTER);

        // Обробник вибору таксопарку
        fleetsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                TaxiFleet selectedFleet = fleetsList.getSelectedValue();
                if (selectedFleet != null) {
                    // Оновлюємо вміст правої панелі
                    panel.removeAll();
                    panel.add(createFleetInfoPanel(selectedFleet), BorderLayout.CENTER);
                    panel.revalidate();
                    panel.repaint();
                }
            }
        });

        return panel;
    }

    /**
     * Створення панелі з кнопками
     */
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // Кнопка додавання нового таксопарку
        JButton addButton = createStyledButton("Додати таксопарк", ACCENT_COLOR);
        addButton.addActionListener(e -> addNewFleet());

        // Кнопка видалення таксопарку
        JButton removeButton = createStyledButton("Видалити", new Color(231, 76, 60));
        removeButton.addActionListener(e -> removeSelectedFleet());

        panel.add(addButton);
        panel.add(removeButton);

        return panel;
    }

    /**
     * Створення стилізованої кнопки
     */
    JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ефект наведення
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darkenColor(bgColor, 0.1f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Створення інформаційної панелі для вибраного таксопарку
     */
    JPanel createFleetInfoPanel(TaxiFleet fleet) {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(PANEL_COLOR);

        // Верхня частина з інформацією про таксопарк
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(PANEL_COLOR);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(0, 0, 15, 0)
        ));

        // Заголовок з назвою таксопарку
        JLabel fleetNameLabel = new JLabel(fleet.getName());
        fleetNameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        fleetNameLabel.setForeground(TEXT_COLOR);

        // Панель з кнопками дій
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(PANEL_COLOR);

        JButton editButton = new JButton("Редагувати");
        editButton.setFont(new Font("Arial", Font.PLAIN, 12));
        editButton.addActionListener(e -> editFleet(fleet));

        JButton viewCarsButton = new JButton("Керувати");
        viewCarsButton.setFont(new Font("Arial", Font.PLAIN, 12));
        viewCarsButton.addActionListener(e -> viewCars(fleet));

        actionsPanel.add(editButton);
        actionsPanel.add(viewCarsButton);

        infoPanel.add(fleetNameLabel, BorderLayout.WEST);
        infoPanel.add(actionsPanel, BorderLayout.EAST);

        // Основна частина з статистикою таксопарку
        StatsPanel statsPanel = new StatsPanel(fleet);

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Оновлення списку таксопарків
     */
    void updateFleetsList() {
        fleetsListModel.clear();
        for (TaxiFleet fleet : fleetManager.getFleets()) {
            fleetsListModel.addElement(fleet);
        }
    }

    /**
     * Додавання нового таксопарку
     */
    void addNewFleet() {
        String fleetName = JOptionPane.showInputDialog(this,
                "Введіть назву нового таксопарку:",
                "Новий таксопарк",
                JOptionPane.PLAIN_MESSAGE);

        if (fleetName != null && !fleetName.trim().isEmpty()) {
            boolean fleetExists = false;
            for (TaxiFleet fleet : fleetManager.getFleets()) {
                if (fleet.getName().equalsIgnoreCase(fleetName.trim())) {
                    fleetExists = true;
                    break;
                }
            }

            if (fleetExists) {
                logger.warn("Attempt to create duplicate fleet: {}", fleetName);
                JOptionPane.showMessageDialog(this,
                        "Таксопарк з назвою '" + fleetName + "' вже існує!\nВведіть унікальну назву.",
                        "Помилка",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                TaxiFleet newFleet = new TaxiFleet(fleetName.trim());
                fleetManager.addFleet(newFleet);
                logger.info("Created new fleet: {}", fleetName);
                updateFleetsList();
                fleetsList.setSelectedValue(newFleet, true);
            }
        }
    }

    /**
     * Видалення вибраного таксопарку
     */
    void removeSelectedFleet() {
        TaxiFleet selectedFleet = fleetsList.getSelectedValue();
        if (selectedFleet != null) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Ви дійсно хочете видалити таксопарк '" + selectedFleet.getName() + "'?",
                    "Підтвердження видалення",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                fleetManager.removeFleet(selectedFleet);
                logger.info("Removed fleet: {}", selectedFleet.getName());
                updateFleetsList();
            }
        } else {
            logger.warn("Attempt to remove fleet with no selection");
            JOptionPane.showMessageDialog(this,
                    "Будь ласка, виберіть таксопарк для видалення",
                    "Попередження",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Редагування таксопарку
     */
    void editFleet(TaxiFleet fleet) {
        String newName = JOptionPane.showInputDialog(this,
                "Введіть нову назву для таксопарку:",
                "Редагування таксопарку",
                JOptionPane.PLAIN_MESSAGE);

        if (newName != null && !newName.trim().isEmpty()) {
            boolean nameExists = false;
            for (TaxiFleet existingFleet : fleetManager.getFleets()) {
                if (existingFleet != fleet &&
                        existingFleet.getName().equalsIgnoreCase(newName.trim())) {
                    nameExists = true;
                    break;
                }
            }

            if (nameExists) {
                logger.warn("Attempt to rename fleet to existing name: {}", newName);
                JOptionPane.showMessageDialog(this,
                        "Таксопарк з назвою '" + newName + "' вже існує!\nВведіть унікальну назву.",
                        "Помилка",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                String oldName = fleet.getName();
                fleet.setName(newName.trim());
                logger.info("Renamed fleet from {} to {}", oldName, newName);
                updateFleetsList();
                fleetsList.setSelectedValue(fleet, true);
            }
        }
    }

    /**
     * Перегляд автомобілів таксопарку
     */
    void viewCars(TaxiFleet fleet) {
        logger.info("Opening car management for fleet: {}", fleet.getName());
        FleetManagementPanel fleetManagementPanel = new FleetManagementPanel(fleet);
        String tabName = fleet.getName();
        mainTabbedPane.addTab(tabName, fleetManagementPanel);
        mainTabbedPane.setSelectedComponent(fleetManagementPanel);
    }

    /**
     * Затемнення кольору для ефектів UI
     */
    Color darkenColor(Color color, float fraction) {
        int red = Math.max(0, Math.round(color.getRed() * (1 - fraction)));
        int green = Math.max(0, Math.round(color.getGreen() * (1 - fraction)));
        int blue = Math.max(0, Math.round(color.getBlue() * (1 - fraction)));
        return new Color(red, green, blue);
    }

    /**
     * Створення заглушки-іконки
     */
    private ImageIcon createPlaceholderIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Малюємо кружечок зі значком таксі
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(220, 220, 220));
        g2.fillOval(0, 0, width, height);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 40));
        g2.drawString("🚖", width/2 - 20, height/2 + 15);

        g2.dispose();
        return new ImageIcon(image);
    }

    /**
     * Кастомний рендерер для списку таксопарків
     */
    static class FleetListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            if (value instanceof TaxiFleet) {
                TaxiFleet fleet = (TaxiFleet) value;

                JLabel nameLabel = new JLabel(fleet.getName());
                nameLabel.setFont(LIST_FONT);

                JLabel countLabel = new JLabel(fleet.getCars().size() + " авто");
                countLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                countLabel.setForeground(Color.GRAY);

                panel.add(nameLabel, BorderLayout.WEST);
                panel.add(countLabel, BorderLayout.EAST);

                if (isSelected) {
                    panel.setBackground(SECONDARY_COLOR);
                    nameLabel.setForeground(Color.WHITE);
                    countLabel.setForeground(new Color(220, 220, 220));
                } else {
                    panel.setBackground(PANEL_COLOR);
                    nameLabel.setForeground(TEXT_COLOR);
                }
            }

            return panel;
        }
    }
}
