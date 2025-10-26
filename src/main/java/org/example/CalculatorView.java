package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * Graphical user interface for the calculator.
 * Implements iOS-style design with dark theme and rounded buttons.
 */
public class CalculatorView {
    // iOS-style color scheme
    private static final Color DARK_BG = new Color(44, 44, 46);
    private static final Color BUTTON_GRAY = new Color(58, 58, 60);
    private static final Color BUTTON_LIGHT_GRAY = new Color(165, 165, 165);
    private static final Color BUTTON_ORANGE = new Color(255, 149, 0);
    private static final Color TEXT_WHITE = Color.WHITE;

    private final Calculator calculator;
    private final JTextField display;

    /**
     * Creates the calculator window and initializes all UI components.
     *
     * @param calculator Calculator instance for business logic
     */
    public CalculatorView(Calculator calculator) {
        this.calculator = calculator;

        // Create main window
        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(320, 480);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(DARK_BG);

        // Create and add display and button panel
        this.display = createDisplay();
        frame.add(display, BorderLayout.NORTH);
        frame.add(createButtonPanel(), BorderLayout.CENTER);

        // Center window on screen and make visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Creates the display text field showing current expression/result.
     * Non-editable, right-aligned with large font.
     *
     * @return Configured JTextField for display
     */
    private JTextField createDisplay() {
        JTextField field = new JTextField("0");
        field.setEditable(false);
        field.setHorizontalAlignment(JTextField.RIGHT);
        field.setFont(new Font("SF Pro Display", Font.PLAIN, 48));
        field.setBackground(DARK_BG);
        field.setForeground(TEXT_WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return field;
    }

    /**
     * Updates display with adaptive font size based on text length.
     * Longer expressions get smaller font to fit in display.
     *
     * @param text Text to display
     */
    private void updateDisplayWithAdaptiveFont(String text) {
        display.setText(text.isEmpty() ? "0" : text);

        // Calculate font size based on text length
        int fontSize;
        int length = display.getText().length();

        if (length <= 9) {
            fontSize = 48;
        } else if (length <= 13) {
            fontSize = 33;
        } else if (length <= 17) {
            fontSize = 25;
        } else if (length <= 21) {
            fontSize = 20;
        } else if (length <= 30) {
            fontSize = 15;
        } else {
            fontSize = 10;
        }

        display.setFont(new Font("SF Pro Display", Font.PLAIN, fontSize));
    }

    /**
     * Creates the button panel with calculator buttons in iOS-style layout.
     * Uses GridBagLayout for flexible button sizing (0 button spans 2 columns).
     *
     * @return JPanel containing all calculator buttons
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(DARK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5); // 5px spacing between buttons
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Row 1: Clear, Plus/Minus, Percent, Division
        addButton(panel, "$", 0, 0, 1, BUTTON_LIGHT_GRAY, Color.BLACK, gbc);
        addButton(panel, "C", 1, 0, 1, BUTTON_LIGHT_GRAY, Color.BLACK, gbc);
        addButton(panel, "%", 2, 0, 1, BUTTON_LIGHT_GRAY, Color.BLACK, gbc);
        addButton(panel, "÷", 3, 0, 1, BUTTON_ORANGE, TEXT_WHITE, gbc);

        // Row 2: 7, 8, 9, Multiplication
        addButton(panel, "7", 0, 1, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "8", 1, 1, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "9", 2, 1, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "×", 3, 1, 1, BUTTON_ORANGE, TEXT_WHITE, gbc);

        // Row 3: 4, 5, 6, Subtraction
        addButton(panel, "4", 0, 2, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "5", 1, 2, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "6", 2, 2, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "−", 3, 2, 1, BUTTON_ORANGE, TEXT_WHITE, gbc);

        // Row 4: 1, 2, 3, Addition
        addButton(panel, "1", 0, 3, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "2", 1, 3, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "3", 2, 3, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "+", 3, 3, 1, BUTTON_ORANGE, TEXT_WHITE, gbc);

        // Row 5: 0 (double width), Decimal, Equals
        addButton(panel, "0", 0, 4, 2, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, ".", 2, 4, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "=", 3, 4, 1, BUTTON_ORANGE, TEXT_WHITE, gbc);

        return panel;
    }

    /**
     * Creates a single button with specified properties and adds it to the panel.
     *
     * @param panel Parent panel to add button to
     * @param label Button label text
     * @param x Grid X position
     * @param y Grid Y position
     * @param width Number of columns to span
     * @param bg Background color
     * @param fg Foreground (text) color
     * @param gbc GridBagConstraints for layout
     */
    private void addButton(JPanel panel, String label, int x, int y, int width,
                           Color bg, Color fg, GridBagConstraints gbc) {
        JButton button = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Цвет заливки в зависимости от состояния (без теней имён)
                    Color fillColor;
                    if (!isEnabled()) {
                        fillColor = bg.darker().darker();
                    } else if (getModel().isPressed()) {
                        fillColor = bg.darker();
                    } else if (getModel().isRollover()) {
                        fillColor = bg.brighter();
                    } else {
                        fillColor = bg;
                    }

                    // Рисуем закруглённый фон
                    int arc = 12;
                    g2.setColor(fillColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                    // Если нужен контур — можно его нарисовать:
                    // g2.setColor(fillColor.darker());
                    // g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);
                } finally {
                    g2.dispose();
                }

                // Рисуем текст и прочее (super не будет заливать фон, так как setOpaque(false))
                super.paintComponent(g);
            }
        };

        // Важно: отключаем стандартную заливку фона, чтобы супер не затирал закруглённый фон
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        // Отступы, чтобы текст не прилегал к краям
        button.setMargin(new Insets(6, 12, 6, 12));
        button.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        button.setFont(new Font("SF Pro Display", Font.PLAIN, 23)); // если шрифт не найден — будет fallback
        button.setBackground(bg); // используется в paintComponent
        button.setForeground(fg);
        button.addActionListener(e -> handleButtonClick(label));

        // Позиционирование в GridBag
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        panel.add(button, gbc);
    }


    /**
     * Handles button click events and updates display.
     * Converts iOS symbols to standard operators before processing.
     *
     * @param cmd Button label that was clicked
     */
    private void handleButtonClick(String cmd) {
        String result;

        // Convert iOS symbols to standard operators
        String normalizedCmd = cmd;
        if ("÷".equals(cmd)) normalizedCmd = "/";
        if ("×".equals(cmd)) normalizedCmd = "*";
        if ("−".equals(cmd)) normalizedCmd = "-";

        // Process button command
        if ("C".equals(cmd)) {
            calculator.clear();
            result = "0";
        } else if ("±".equals(cmd)) {
            // TODO: Implement sign toggle
            return;
        } else if ("%".equals(cmd)) {
            // TODO: Implement percentage
            return;
        } else if (normalizedCmd.matches("[0-9.]")) {
            // Handle digit or decimal point
            result = calculator.handleDigitInput(normalizedCmd);
        } else if (normalizedCmd.matches("[+\\-*/]")) {
            // Handle operator
            result = calculator.handleOperatorInput(normalizedCmd);
        } else if ("=".equals(cmd)) {
            // Calculate result
            result = calculator.calculateResult();
        } else {
            return;
        }

        // Update display with adaptive font
        updateDisplayWithAdaptiveFont(result);
    }
}