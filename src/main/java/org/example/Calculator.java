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
            lastWasResult = false;
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

    /**
     * Returns current expression as string.
     */
    public String getExpression() {
        return expression.toString();
    }

    /**
     * Returns whether last operation was a result.
     */
    public boolean isLastWasResult() {
        return lastWasResult;
    }

    /**
     * Toggles the sign of the current number (or result).
     * Works like iOS calculator: negates the last number in the expression.
     */
    public String negate() {
        if (expression.isEmpty()) {
            return "0";
        }

        // Find the start of the last number
        int end = expression.length();
        int start = end - 1;

        // Move backwards to find the start of the number
        while (start > 0) {
            char c = expression.charAt(start - 1);
            if (isOperator(c)) {
                break;
            }
            start--;
        }

        // Extract the number
        String numStr = expression.substring(start, end);

        // Handle empty or invalid cases
        if (numStr.isEmpty() || ".".equals(numStr)) {
            return expression.toString();
        }

        try {
            BigDecimal num = new BigDecimal(numStr);
            BigDecimal negated = num.negate();
            String negatedStr = negated.stripTrailingZeros().toPlainString();

            // Replace the number in the expression
            expression.replace(start, end, negatedStr);

            return expression.toString();
        } catch (NumberFormatException e) {
            return expression.toString();
        }
    }

    /**
     * Converts the current number to percentage (divides by 100).
     * Works like iOS calculator: converts last number to its percentage value.
     */
    public String percentage() {
        if (expression.isEmpty()) {
            return "0";
        }

        // Find the start of the last number
        int end = expression.length();
        int start = end - 1;

        // Move backwards to find the start of the number
        while (start > 0) {
            char c = expression.charAt(start - 1);
            if (isOperator(c)) {
                break;
            }
            start--;
        }

        // Extract the number
        String numStr = expression.substring(start, end);

        // Handle empty or invalid cases
        if (numStr.isEmpty() || ".".equals(numStr)) {
            return expression.toString();
        }

        try {
            BigDecimal num = new BigDecimal(numStr);
            BigDecimal percent = num.divide(new BigDecimal("100"));
            String percentStr = percent.stripTrailingZeros().toPlainString();

            // Replace the number in the expression
            expression.replace(start, end, percentStr);

            return expression.toString();
        } catch (NumberFormatException e) {
            return expression.toString();
        }
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