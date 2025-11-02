package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * iOS-style calculator UI with keyboard support.
 */
public class CalculatorView {
    private static final Color DARK_BG = new Color(44, 44, 46);
    private static final Color BTN_GRAY = new Color(58, 58, 60);
    private static final Color BTN_LIGHT = new Color(165, 165, 165);
    private static final Color BTN_ORANGE = new Color(255, 149, 0);
    private static final int WIDTH = 320;
    private static final int HEIGHT = 480;

    private final Calculator calculator;
    private final DisplayFormatter formatter = new DisplayFormatter();
    private final JTextField display;
    private final JLabel expressionLabel;

    public CalculatorView(Calculator calculator) {
        this.calculator = calculator;

        JFrame frame = createFrame();
        this.display = createDisplay();
        this.expressionLabel = createExpressionLabel();

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(DARK_BG);
        displayPanel.add(expressionLabel, BorderLayout.NORTH);
        displayPanel.add(display, BorderLayout.CENTER);

        frame.add(displayPanel, BorderLayout.NORTH);
        frame.add(createButtons(), BorderLayout.CENTER);
        setupKeyboard(frame);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JFrame createFrame() {
        JFrame f = new JFrame("Calculator");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(WIDTH, HEIGHT);
        f.setLayout(new BorderLayout());
        f.getContentPane().setBackground(DARK_BG);
        return f;
    }

    private JTextField createDisplay() {
        JTextField f = new JTextField("0");
        f.setEditable(false);
        f.setHorizontalAlignment(JTextField.RIGHT);
        f.setFont(new Font("SF Pro Display", Font.PLAIN, 48));
        f.setBackground(DARK_BG);
        f.setForeground(Color.WHITE);
//        f.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        return f;
    }

    private JLabel createExpressionLabel() {
        JLabel label = new JLabel(" ");
        label.setHorizontalAlignment(JLabel.RIGHT);
        label.setFont(new Font("SF Pro Display", Font.PLAIN, 16));
        label.setForeground(new Color(150, 150, 150));
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        return label;
    }

    private void updateDisplay(String text) {
        String formatted = formatter.format(text.isEmpty() ? "0" : text);
        display.setText(formatted);

        // Adaptive font size
        int len = formatted.length();
        int size = len <= 9 ? 48 : len <= 13 ? 33 : len <= 17 ? 25 : len <= 21 ? 20 : len <= 30 ? 15 : 10;
        display.setFont(new Font("SF Pro Display", Font.PLAIN, size));
    }

    private void setupKeyboard(JFrame frame) {
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String key = getKeyMapping(e);
                if (key != null) handleInput(key);
            }
        });
        frame.setFocusable(true);
    }

    private String getKeyMapping(KeyEvent e) {
        char c = e.getKeyChar();
        int code = e.getKeyCode();

        if (Character.isDigit(c) || c == '.') return String.valueOf(c);
        if (c == '+' || c == '-' || c == '*' || c == '/') return String.valueOf(c);
        if (c == '=' || code == KeyEvent.VK_ENTER) return "=";
        if (c == 'c' || c == 'C' || code == KeyEvent.VK_ESCAPE) return "C";
        if (code == KeyEvent.VK_BACK_SPACE) return "⌫";
        if (c == '%') return "%";
        if (c == 'n' || c == 'N') return "±"; // 'n' for negate

        return null;
    }

    private JPanel createButtons() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(DARK_BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH;
        g.insets = new Insets(5, 5, 5, 5);
        g.weightx = g.weighty = 1;

        // Row 1
        btn(p, "C", 0, 0, 1, BTN_LIGHT, Color.BLACK, g);
        btn(p, "±", 1, 0, 1, BTN_LIGHT, Color.BLACK, g);
        btn(p, "%", 2, 0, 1, BTN_LIGHT, Color.BLACK, g);
        btn(p, "÷", 3, 0, 1, BTN_ORANGE, Color.WHITE, g);

        // Row 2
        btn(p, "7", 0, 1, 1, BTN_GRAY, Color.WHITE, g);
        btn(p, "8", 1, 1, 1, BTN_GRAY, Color.WHITE, g);
        btn(p, "9", 2, 1, 1, BTN_GRAY, Color.WHITE, g);
        btn(p, "×", 3, 1, 1, BTN_ORANGE, Color.WHITE, g);

        // Row 3
        btn(p, "4", 0, 2, 1, BTN_GRAY, Color.WHITE, g);
        btn(p, "5", 1, 2, 1, BTN_GRAY, Color.WHITE, g);
        btn(p, "6", 2, 2, 1, BTN_GRAY, Color.WHITE, g);
        btn(p, "−", 3, 2, 1, BTN_ORANGE, Color.WHITE, g);

        // Row 4
        btn(p, "1", 0, 3, 1, BTN_GRAY, Color.WHITE, g);
        btn(p, "2", 1, 3, 1, BTN_GRAY, Color.WHITE, g);
        btn(p, "3", 2, 3, 1, BTN_GRAY, Color.WHITE, g);
        btn(p, "+", 3, 3, 1, BTN_ORANGE, Color.WHITE, g);

        // Row 5
        btn(p, "0", 0, 4, 2, BTN_GRAY, Color.WHITE, g);
        btn(p, ".", 2, 4, 1, BTN_GRAY, Color.WHITE, g);
        btn(p, "=", 3, 4, 1, BTN_ORANGE, Color.WHITE, g);

        return p;
    }

    private void btn(JPanel p, String label, int x, int y, int w, Color bg, Color fg, GridBagConstraints g) {
        JButton b = new RoundedButton(label, bg, fg);
        b.addActionListener(e -> handleInput(label));
        g.gridx = x;
        g.gridy = y;
        g.gridwidth = w;
        p.add(b, g);
    }

    private void handleInput(String cmd) {
        String result;
        String previousExpression = null;

        // Normalize operators
        if ("÷".equals(cmd)) cmd = "/";
        if ("×".equals(cmd)) cmd = "*";
        if ("−".equals(cmd)) cmd = "-";

        if ("C".equals(cmd)) {
            calculator.clear();
            result = "0";
            expressionLabel.setText(" ");
        } else if ("⌫".equals(cmd)) {
            result = calculator.backspace();
        } else if ("±".equals(cmd)) {
            result = calculator.negate();
        } else if ("%".equals(cmd)) {
            result = calculator.percentage();
        } else if (cmd.matches("[0-9.]")) {
            result = calculator.input(cmd);
        } else if (cmd.matches("[+\\-*/]")) {
            result = calculator.operator(cmd);
        } else if ("=".equals(cmd)) {
            previousExpression = calculator.getExpression();
            result = calculator.calculate();

            // Показываем выражение только если был реальный расчет
            if (previousExpression != null && !previousExpression.isEmpty() &&
                    !previousExpression.equals(result)) {
                String displayExpr = previousExpression
                        .replace("*", "×")
                        .replace("/", "÷")
                        .replace("-", "−");
                expressionLabel.setText(displayExpr);
            }
        } else {
            return;
        }

        updateDisplay(result);
    }

    /**
     * Custom rounded button with iOS styling.
     */
    private static class RoundedButton extends JButton {
        private final Color bg;

        RoundedButton(String text, Color bg, Color fg) {
            super(text);
            this.bg = bg;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("SF Pro Display", Font.PLAIN, 27));
            setForeground(fg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color c = bg;
            if (getModel().isPressed()) c = bg.darker();
            else if (getModel().isRollover()) c = bg.brighter();

            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();

            super.paintComponent(g);
        }
    }
}