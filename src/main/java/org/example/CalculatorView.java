package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * GUI Calculators
 */
public class CalculatorView {
    private static final Color DARK_BG = new Color(44, 44, 46);
    private static final Color BUTTON_GRAY = new Color(58, 58, 60);
    private static final Color BUTTON_LIGHT_GRAY = new Color(165, 165, 165);
    private static final Color BUTTON_ORANGE = new Color(255, 149, 0);
    private static final Color TEXT_WHITE = Color.WHITE;

    private final Calculator calculator;
    private final JTextField display;

    public CalculatorView(Calculator calculator) {
        this.calculator = calculator;

        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(325, 485);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(DARK_BG);

        this.display = createDisplay();
        frame.add(display, BorderLayout.NORTH);
        frame.add(createButtonPanel(), BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

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

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(DARK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Row 1
        addButton(panel, "<", 0, 0, 1, BUTTON_LIGHT_GRAY, Color.BLACK, gbc);
        addButton(panel, "C", 1, 0, 1, BUTTON_LIGHT_GRAY, Color.BLACK, gbc);
        addButton(panel, "%", 2, 0, 1, BUTTON_LIGHT_GRAY, Color.BLACK, gbc);
        addButton(panel, "÷", 3, 0, 1, BUTTON_ORANGE, TEXT_WHITE, gbc);

        // Row 2
        addButton(panel, "7", 0, 1, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "8", 1, 1, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "9", 2, 1, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "×", 3, 1, 1, BUTTON_ORANGE, TEXT_WHITE, gbc);

        // Row 3
        addButton(panel, "4", 0, 2, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "5", 1, 2, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "6", 2, 2, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "−", 3, 2, 1, BUTTON_ORANGE, TEXT_WHITE, gbc);

        // Row 4
        addButton(panel, "1", 0, 3, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "2", 1, 3, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "3", 2, 3, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "+", 3, 3, 1, BUTTON_ORANGE, TEXT_WHITE, gbc);

        // Row 5
        addButton(panel, "0", 0, 4, 2, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, ".", 2, 4, 1, BUTTON_GRAY, TEXT_WHITE, gbc);
        addButton(panel, "=", 3, 4, 1, BUTTON_ORANGE, TEXT_WHITE, gbc);

        return panel;
    }

    private void addButton(JPanel panel, String label, int x, int y, int width,
                           Color bg, Color fg, GridBagConstraints gbc) {
        JButton button = new JButton(label);
        button.setFont(new Font("SF Pro Display", Font.PLAIN, 28));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.addActionListener(e -> handleButtonClick(label));

        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;

        panel.add(button, gbc);
    }

    private void handleButtonClick(String cmd) {
        String result;

        // Преобразуем символы в стандартные операторы
        String normalizedCmd = cmd;
        if ("÷".equals(cmd)) normalizedCmd = "/";
        if ("×".equals(cmd)) normalizedCmd = "*";
        if ("−".equals(cmd)) normalizedCmd = "-";

        if ("C".equals(cmd)) {
            calculator.clear();
            result = "0";
        } else if ("+/-".equals(cmd)) {
            // todo
            return;
        } else if ("%".equals(cmd)) {
            // todo
            return;
        } else if (normalizedCmd.matches("[0-9.]")) {
            result = calculator.handleDigitInput(normalizedCmd);
        } else if (normalizedCmd.matches("[+\\-*/]")) {
            result = calculator.handleOperatorInput(normalizedCmd);
        } else if (normalizedCmd.matches("<")) {
            result = calculator.handleOperatorBackspace();
        } else if ("=".equals(cmd)) {
            result = calculator.calculateResult();
        } else {
            return;
        }

        display.setText(result.isEmpty() ? "0" : result);
    }
}