package gui.panel;

import gui.dialog.CarFormDialog;
import models.TaxiFleet;
import models.cars.Car;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CarListPanel extends JPanel {
    private static final Logger logger = LogManager.getLogger(CarListPanel.class);
    private TaxiFleet taxiFleet;
    private JTable carTable;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private String currentSort = "За замовчуванням";
    private String currentFuelFilter = "Усі";
    private Double minSpeedFilter = null;
    private Double maxSpeedFilter = null;
    private String searchQuery = "";
    private List<Car> filteredCars;

    // Константи для кольорів та шрифтів
    private static final Color PRIMARY_COLOR = new Color(60, 141, 188);
    private static final Color SECONDARY_COLOR = new Color(245, 245, 245);
    private static final Color ACCENT_COLOR = new Color(0, 166, 90);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(221, 75, 57);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 12);
    private static final Font TABLE_FONT = new Font("SansSerif", Font.PLAIN, 12);

    public CarListPanel(TaxiFleet taxiFleet) {
        this.taxiFleet = taxiFleet;
        this.filteredCars = new ArrayList<>();
        setBackground(SECONDARY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        initComponents();
        loadAndSortCars();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));

        // Нова секція заголовка з назвою таксопарку та кнопками
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Модель таблиці
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3 || columnIndex == 4) {
                    return Double.class; // Тільки ціна і швидкість - числові колонки
                }
                return String.class;
            }
        };
        model.setColumnIdentifiers(new String[]{"Марка", "Модель", "Тип палива", "Ціна", "Швидкість", "Витрата"});

        carTable = new JTable(model);
        sorter = new TableRowSorter<>(model);
        carTable.setRowSorter(sorter);

        // Стилізація таблиці
        carTable.setFont(TABLE_FONT);
        carTable.setRowHeight(25);
        carTable.setSelectionBackground(PRIMARY_COLOR);
        carTable.setSelectionForeground(Color.WHITE);
        carTable.setShowGrid(false);
        carTable.setIntercellSpacing(new Dimension(0, 0));
        carTable.setFillsViewportHeight(true);

        // Замініть поточний рендерер на такий, що обробляє всі типи колонок
        carTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }

                // Для числових колонок: встановлюємо вирівнювання по центру або лівому краю
                if (table.getColumnClass(column).equals(Double.class)) {
                    setHorizontalAlignment(JLabel.LEFT); // або CENTER
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                }

                return c;
            }
        });

        // Додано спеціальний рендерер для Double
        carTable.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }

                // Встановлюємо вирівнювання для числових значень
                setHorizontalAlignment(JLabel.LEFT); // або CENTER

                // Форматування числових значень (опціонально)
                if (value instanceof Double) {
                    if (column == 3) { // Ціна
                        setText(String.format("%.2f $", (Double)value));
                    } else if (column == 4) { // Швидкість
                        setText(String.format("%.1f км/год", (Double)value));
                    }
                }

                return c;
            }
        });

        // Заголовок таблиці
        JTableHeader header = carTable.getTableHeader();
        header.setFont(LABEL_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 30));

        // Панель фільтрів та сортування
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBackground(SECONDARY_COLOR);
        filterPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                "Фільтри та сортування",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                LABEL_FONT,
                PRIMARY_COLOR));

        // Панель пошуку
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        searchPanel.setBackground(SECONDARY_COLOR);

        JPanel searchFieldPanel = createLabeledComponentPanel("Пошук за маркою/моделлю:", new Color(240, 240, 240));
        JTextField searchField = new JTextField(20);
        styleTextField(searchField);
        searchField.setPreferredSize(new Dimension(200, 25));

        // Додаємо DocumentListener для реагування на зміну тексту в полі пошуку
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch();
            }

            private void updateSearch() {
                searchQuery = searchField.getText().trim().toLowerCase();
                applyFiltersAndSorting();
            }
        });

        // Додаємо можливість очистити пошук клавішею Escape
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    searchField.setText("");
                    searchQuery = "";
                    applyFiltersAndSorting();
                }
            }
        });

        searchFieldPanel.add(searchField);

        // Додаємо searchFieldPanel до searchPanel
        searchPanel.add(searchFieldPanel);

        // Верхній ряд фільтрів
        JPanel topFilterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        topFilterRow.setBackground(SECONDARY_COLOR);

        // Фільтр за типом палива
        JPanel fuelFilterPanel = createLabeledComponentPanel("Тип палива:", new Color(240, 240, 240));
        JComboBox<String> fuelTypeFilter = new JComboBox<>(new String[]{"Усі", "Бензин", "Дизель", "Електричний"});
        styleComboBox(fuelTypeFilter);
        fuelTypeFilter.addActionListener(e -> {
            currentFuelFilter = (String) fuelTypeFilter.getSelectedItem();
            applyFiltersAndSorting();
        });
        fuelFilterPanel.add(fuelTypeFilter);

        // Сортування
        JPanel sortPanel = createLabeledComponentPanel("Сортування:", new Color(240, 240, 240));
        JComboBox<String> sortComboBox = new JComboBox<>(new String[]{
                "За замовчуванням",
                "За ціною (зростання)",
                "За ціною (спадання)",
                "За швидкістю (зростання)",
                "За швидкістю (спадання)",
                "За витратою (зростання)",
                "За витратою (спадання)"
        });
        styleComboBox(sortComboBox);
        sortComboBox.addActionListener(e -> {
            currentSort = (String) sortComboBox.getSelectedItem();
            applyFiltersAndSorting();
        });
        sortPanel.add(sortComboBox);

        topFilterRow.add(fuelFilterPanel);
        topFilterRow.add(sortPanel);

        // Нижній ряд фільтрів (діапазон швидкості)
        JPanel speedFilterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        speedFilterRow.setBackground(SECONDARY_COLOR);

        JPanel speedRangePanel = createLabeledComponentPanel("Діапазон швидкості:", new Color(240, 240, 240));
        JPanel speedInputs = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        speedInputs.setBackground(new Color(240, 240, 240));

        JTextField minSpeedField = new JTextField(5);
        JTextField maxSpeedField = new JTextField(5);
        styleTextField(minSpeedField);
        styleTextField(maxSpeedField);

        speedInputs.add(new JLabel("від:"));
        speedInputs.add(minSpeedField);
        speedInputs.add(new JLabel("до:"));
        speedInputs.add(maxSpeedField);

        JButton speedFilterBtn = new JButton("Застосувати");
        styleButton(speedFilterBtn, ACCENT_COLOR);
        speedFilterBtn.addActionListener(e -> {
            try {
                minSpeedFilter = minSpeedField.getText().isEmpty() ? null : Double.parseDouble(minSpeedField.getText());
                maxSpeedFilter = maxSpeedField.getText().isEmpty() ? null : Double.parseDouble(maxSpeedField.getText());
                applyFiltersAndSorting();
            } catch (NumberFormatException ex) {
                showErrorMessage("Невірний формат швидкості", "Введіть коректне числове значення");
            }
        });

        speedInputs.add(Box.createHorizontalStrut(10));
        speedInputs.add(speedFilterBtn);
        speedRangePanel.add(speedInputs);

        JButton resetFiltersBtn = new JButton("Скинути фільтри");
        styleButton(resetFiltersBtn, WARNING_COLOR);
        resetFiltersBtn.addActionListener(e -> {
            resetFilters(fuelTypeFilter, sortComboBox, minSpeedField, maxSpeedField, searchField);
        });

        speedFilterRow.add(speedRangePanel);
        speedFilterRow.add(resetFiltersBtn);

        // Додаємо панелі в основну панель фільтрів
        filterPanel.add(searchPanel);
        filterPanel.add(topFilterRow);
        filterPanel.add(speedFilterRow);

        // Таблиця з прокруткою
        JScrollPane tableScrollPane = new JScrollPane(carTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        tableScrollPane.getViewport().setBackground(Color.WHITE);

        // Кнопки керування в нижній частині (оновлення даних)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setBackground(SECONDARY_COLOR);

        JButton refreshBtn = new JButton("Оновити дані");
        styleButton(refreshBtn, PRIMARY_COLOR);
        refreshBtn.setIcon(UIManager.getIcon("FileView.refreshIcon"));
        refreshBtn.addActionListener(e -> loadAndSortCars());

        buttonPanel.add(refreshBtn);

        // Створюємо контейнер для фільтрів і таблиці
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(SECONDARY_COLOR);
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Додаємо всі компоненти
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Додаємо подвійний клік на рядку таблиці
        carTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showCarDetails();
                }
            }
        });
    }

    // Створення нової секції заголовка з назвою та кнопками
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94)); // Темно-синій колір для заголовка
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Заголовок таксопарку - використовуємо назву з об'єкта таксопарку
        JLabel titleLabel = new JLabel(taxiFleet.getName());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon")); // Можна замінити на власну іконку
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // Панель з кнопками
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        // Кнопка додавання авто
        JButton addCarButton = new JButton("Додати авто");
        styleHeaderButton(addCarButton, ACCENT_COLOR);
        addCarButton.setIcon(UIManager.getIcon("FileView.fileIcon"));
        addCarButton.addActionListener(e -> openAddCarDialog());

        // Кнопка видалення авто
        JButton removeCarButton = new JButton("Видалити авто");
        styleHeaderButton(removeCarButton, DANGER_COLOR);
        removeCarButton.setIcon(UIManager.getIcon("FileChooser.detailsViewIcon"));
        removeCarButton.addActionListener(e -> removeSelectedCar());

        buttonPanel.add(addCarButton);
        buttonPanel.add(removeCarButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    // Стилізація кнопок заголовка
    private void styleHeaderButton(JButton button, Color color) {
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 35));

        // Додаємо відступи для іконки
        button.setIconTextGap(8);
        button.setMargin(new Insets(0, 10, 0, 10));
    }

    private void openAddCarDialog() {
        logger.info("Opening add car dialog");
        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        CarFormDialog dialog = new CarFormDialog(mainFrame, taxiFleet, this);
        dialog.setVisible(true);
    }

    private void removeSelectedCar() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow >= 0) {
            int viewRow = carTable.getSelectedRow();
            int modelRow = carTable.convertRowIndexToModel(viewRow);

            if (modelRow < filteredCars.size()) {
                Car carToRemove = filteredCars.get(modelRow);
                logger.info("Attempting to remove car: {} {}", carToRemove.getMake(), carToRemove.getModel());

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        String.format("Ви дійсно бажаєте видалити %s %s?",
                                carToRemove.getMake(), carToRemove.getModel()),
                        "Підтвердження видалення",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        taxiFleet.removeCar(carToRemove);
                        loadAndSortCars();
                        logger.info("Car removed successfully: {} {}", carToRemove.getMake(), carToRemove.getModel());
                        JOptionPane.showMessageDialog(
                                this,
                                "Автомобіль успішно видалено",
                                "Інформація",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        logger.error("Failed to remove car: {} {}", carToRemove.getMake(), carToRemove.getModel(), e);
                        showErrorMessage("Помилка видалення", "Не вдалося видалити автомобіль: " + e.getMessage());
                    }
                }
            }
        } else {
            logger.warn("No car selected for removal");
            JOptionPane.showMessageDialog(
                    this,
                    "Будь ласка, виберіть автомобіль для видалення",
                    "Попередження",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showCarDetails() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = carTable.convertRowIndexToModel(selectedRow);

            if (modelRow < filteredCars.size()) {
                Car selectedCar = filteredCars.get(modelRow);
                JOptionPane.showMessageDialog(this,
                        String.format("<html><b>Детальна інформація</b><br><br>" +
                                        "Марка: %s<br>" +
                                        "Модель: %s<br>" +
                                        "Тип палива: %s<br>" +
                                        "Ціна: %.2f $<br>" +
                                        "Макс. швидкість: %.1f км/год<br>" +
                                        "Витрата палива: %.1f %s<br></html>",
                                selectedCar.getMake(),
                                selectedCar.getModel(),
                                selectedCar.getFuelType(),
                                selectedCar.getPrice(),
                                selectedCar.getMaxSpeed(),
                                selectedCar.getFuelConsumption(),
                                selectedCar.getFuelType().equals("Електричний") ? "кВт·год" : "л/100км"),
                        "Інформація про автомобіль",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private JPanel createLabeledComponentPanel(String labelText, Color backgroundColor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(backgroundColor);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(PRIMARY_COLOR);

        panel.add(label);
        return panel;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(TABLE_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        comboBox.setPreferredSize(new Dimension(200, 25));
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(TABLE_FONT);
        textField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        textField.setPreferredSize(new Dimension(60, 25));
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 30));
    }

    private void resetFilters(JComboBox<String> fuelTypeFilter, JComboBox<String> sortComboBox,
                              JTextField minSpeedField, JTextField maxSpeedField, JTextField searchField) {
        fuelTypeFilter.setSelectedItem("Усі");
        sortComboBox.setSelectedItem("За замовчуванням");
        minSpeedField.setText("");
        maxSpeedField.setText("");
        searchField.setText("");

        currentFuelFilter = "Усі";
        currentSort = "За замовчуванням";
        minSpeedFilter = null;
        maxSpeedFilter = null;
        searchQuery = "";

        applyFiltersAndSorting();
    }

    private void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    private void applyFiltersAndSorting() {
        List<Car> cars = new ArrayList<>(taxiFleet.getCars());

        if (!searchQuery.isEmpty()) {
            List<Car> filtered = new ArrayList<>();
            for (Car car : cars) {
                String make = car.getMake().toLowerCase();
                String model = car.getModel().toLowerCase();

                // Перевіряємо, чи починається марка або модель з пошукового запиту
                // або чи є це окремим словом у марці чи моделі
                if (make.startsWith(searchQuery) ||
                        model.startsWith(searchQuery) ||
                        make.contains(" " + searchQuery) ||
                        model.contains(" " + searchQuery)) {
                    filtered.add(car);
                }
            }
            cars = filtered;
        }

        // Фільтрація за типом палива
        if (!"Усі".equals(currentFuelFilter)) {
            List<Car> filtered = new ArrayList<>();
            for (Car car : cars) {
                if (car.getFuelType().equals(currentFuelFilter)) {
                    filtered.add(car);
                }
            }
            cars = filtered;
        }

        // Фільтрація за швидкістю
        if (minSpeedFilter != null) {
            List<Car> filtered = new ArrayList<>();
            for (Car car : cars) {
                if (car.getMaxSpeed() >= minSpeedFilter) {
                    filtered.add(car);
                }
            }
            cars = filtered;
        }

        if (maxSpeedFilter != null) {
            List<Car> filtered = new ArrayList<>();
            for (Car car : cars) {
                if (car.getMaxSpeed() <= maxSpeedFilter) {
                    filtered.add(car);
                }
            }
            cars = filtered;
        }

        // Сортування
        cars = sortCars(cars);

        // Зберігаємо відфільтрований список
        this.filteredCars = cars;

        // Оновлення таблиці
        updateTable(cars);
    }

    private List<Car> sortCars(List<Car> cars) {
        List<Car> sortedCars = new ArrayList<>(cars);

        switch (currentSort) {
            case "За ціною (зростання)":
                sortedCars.sort(Comparator.comparingDouble(Car::getPrice));
                break;
            case "За ціною (спадання)":
                sortedCars.sort((c1, c2) -> Double.compare(c2.getPrice(), c1.getPrice()));
                break;
            case "За швидкістю (зростання)":
                sortedCars.sort(Comparator.comparingDouble(Car::getMaxSpeed));
                break;
            case "За швидкістю (спадання)":
                sortedCars.sort((c1, c2) -> Double.compare(c2.getMaxSpeed(), c1.getMaxSpeed()));
                break;
            case "За витратою (зростання)":
                sortedCars.sort(Comparator.comparingDouble(Car::getFuelConsumption));
                break;
            case "За витратою (спадання)":
                sortedCars.sort((c1, c2) -> Double.compare(c2.getFuelConsumption(), c1.getFuelConsumption()));
                break;
            default:
                // За замовчуванням не сортуємо
                break;
        }

        return sortedCars;
    }

    private void updateTable(List<Car> cars) {
        model.setRowCount(0);

        for (Car car : cars) {
            // Використовуємо тільки числові типи для числових колонок
            String fuelConsumptionStr = String.format("%.1f %s",
                    car.getFuelConsumption(),
                    car.getFuelType().equals("Електричний") ? "кВт·год" : "л/100км");

            model.addRow(new Object[]{
                    car.getMake(),
                    car.getModel(),
                    car.getFuelType(),
                    car.getPrice(),
                    car.getMaxSpeed(),
                    fuelConsumptionStr // використовуємо рядок замість комбінації числа і рядка
            });
        }

        // Статус у нижньому куті
        SwingUtilities.invokeLater(() -> {
            firePropertyChange("statusMessage", null,
                    String.format("Відображено %d автомобілів із %d",
                            filteredCars.size(), taxiFleet.getCars().size()));
        });
    }

    public void loadAndSortCars() {
        try {
            logger.info("Loading and sorting cars");
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            taxiFleet.loadCarsFromDatabase();
            applyFiltersAndSorting();
            logger.info("Cars loaded and sorted successfully");
        } catch (Exception e) {
            logger.error("Failed to load and sort cars", e);
            showErrorMessage("Помилка завантаження даних", "Не вдалося завантажити дані: " + e.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
}