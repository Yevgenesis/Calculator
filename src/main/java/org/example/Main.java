package org.example;

import javax.swing.SwingUtilities;

/**
 * Entry point of the calculator application.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorView(new Calculator()));
    }
}