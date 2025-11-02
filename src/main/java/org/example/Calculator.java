package org.example;

import java.math.BigDecimal;

/**
 * Calculator business logic with expression management.
 * Delegates evaluation to ExpressionEvaluator.
 */
public class Calculator {
    private final StringBuilder expression = new StringBuilder();
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();
    private boolean lastWasResult = false;

    /**
     * Handles digit or decimal point input.
     */
    public String input(String value) {
        if (lastWasResult) {
            clear();
        }

        if (".".equals(value)) {
            addDecimalPoint();
        } else {
            expression.append(value);
        }

        return expression.toString();
    }

    /**
     * Handles operator input, replacing last operator if needed.
     */
    public String operator(String op) {
        lastWasResult = false;

        if (expression.isEmpty()) {
            if ("-".equals(op)) expression.append(op);
            return expression.toString();
        }

        int lastIdx = expression.length() - 1;
        char last = expression.charAt(lastIdx);

        if (isOperatorOrDot(last)) {
            expression.setCharAt(lastIdx, op.charAt(0));
        } else {
            expression.append(op);
        }

        return expression.toString();
    }

    /**
     * Evaluates the expression and returns result.
     */
    public String calculate() {
        if (lastWasResult || expression.isEmpty()) {
            return expression.toString();
        }

        // Remove trailing operator
        int lastIdx = expression.length() - 1;
        if (isOperator(expression.charAt(lastIdx))) {
            expression.deleteCharAt(lastIdx);
        }

        try {
            BigDecimal result = evaluator.evaluate(expression.toString());
            String formatted = result.stripTrailingZeros().toPlainString();

            expression.setLength(0);
            expression.append(formatted);
            lastWasResult = true;

            return formatted;
        } catch (ArithmeticException e) {
            clear();
            return "Undefined";
        } catch (Exception e) {
            clear();
            return "Error";
        }
    }

    /**
     * Deletes last character from expression.
     */
    public String backspace() {
        if (!expression.isEmpty()) {
            expression.deleteCharAt(expression.length() - 1);
        }
        return expression.isEmpty() ? "0" : expression.toString();
    }

    /**
     * Clears expression.
     */
    public void clear() {
        expression.setLength(0);
        lastWasResult = false;
    }

    private void addDecimalPoint() {
        if (expression.isEmpty() || isOperator(expression.charAt(expression.length() - 1))) {
            expression.append("0");
        }

        // Check if current number already has decimal
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (isOperator(c)) break;
            if (c == '.') return; // Already has decimal
        }

        expression.append(".");
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean isOperatorOrDot(char c) {
        return isOperator(c) || c == '.';
    }
}