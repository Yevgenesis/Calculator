package org.example;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calculator business logic using BigDecimal for precise arithmetic operations.
 * Supports basic operations: addition, subtraction, multiplication, and division.
 * Features thousand separators for better readability of large numbers.
 */
public class Calculator {
    // Precision context: 34 significant digits with HALF_UP rounding mode
    private static final MathContext MC = new MathContext(34, RoundingMode.HALF_UP);

    // Maximum number of decimal places to display
    private static final int DISPLAY_SCALE = 10;

    // Regular expression pattern for mathematical operators
    private static final String OPERATORS_REGEX = "[+\\-*/]";

    // Current mathematical expression being built
    private final StringBuilder expression = new StringBuilder();

    // Flag indicating if the last operation was a calculation (for clearing on next input)
    private boolean lastWasResult = false;

    /**
     * Handles digit or decimal point input.
     * Automatically formats numbers with thousand separators (spaces) for readability.
     *
     * @param input Single digit (0-9) or decimal point (.)
     * @return Current expression as string
     */
    public String handleDigitInput(String input) {
        // Clear expression if continuing after a result
        if (lastWasResult) {
            clear();
        }

        if (".".equals(input)) {
            handleDecimalPoint();
        } else {
            String lastNumber = getLastNumber();

            // If number is less than 3 digits or contains decimal point - append directly
            // Otherwise format with thousand separators
            if (lastNumber.contains(".") || lastNumber.length() < 3) {
                expression.append(input);
            } else {
                // Add digit and reformat with spaces every 3 digits
                replaceLastNumber(lastNumber + input);
            }
        }

        return expression.toString();
    }

    /**
     * Handles operator input (+, -, *, /).
     * Replaces the last operator if multiple operators are pressed consecutively.
     * Allows leading minus for negative numbers.
     *
     * @param operator Mathematical operator
     * @return Current expression as string
     */
    public String handleOperatorInput(String operator) {
        lastWasResult = false;

        // Allow leading minus sign for negative numbers
        if (expression.isEmpty()) {
            if ("-".equals(operator)) {
                expression.append(operator);
            }
            return expression.toString();
        }

        // Replace last character if it's an operator or decimal point
        char lastChar = expression.charAt(expression.length() - 1);
        if (isOperatorOrDot(lastChar)) {
            expression.deleteCharAt(expression.length() - 1);
        }

        expression.append(operator);
        return expression.toString();
    }

    /**
     * Evaluates the current mathematical expression using BigDecimal.
     * Follows standard order of operations (multiplication/division before addition/subtraction).
     * Formats result with thousand separators and removes trailing zeros.
     *
     * @return Calculated result or error message
     */
    public String calculateResult() {
        // Return current expression if already showing a result or empty
        if (lastWasResult || expression.isEmpty()) {
            return expression.toString();
        }

        // Remove trailing operator if present
        char lastChar = expression.charAt(expression.length() - 1);
        if (isOperator(lastChar)) {
            expression.deleteCharAt(expression.length() - 1);
        }

        try {
            // Remove spaces before calculation
            String cleanExpr = cleanExpression(expression.toString());

            // Evaluate expression following order of operations
            BigDecimal result = evaluateExpression(cleanExpr);

            // Format result with thousand separators
            String formatted = formatResult(result);

            // Store result as new expression
            expression.setLength(0);
            expression.append(formatted);
            lastWasResult = true;

            return formatted;
        } catch (ArithmeticException e) {
            // Handle division by zero
            clear();
            return "Undefined";
        } catch (Exception e) {
            // Handle any other calculation errors
            clear();
            return "Error";
        }
    }

    /**
     * Clears the current expression and resets state.
     */
    public void clear() {
        expression.setLength(0);
        lastWasResult = false;
    }

    // ==================== Expression manipulation methods ====================

    /**
     * Extracts the last number from the expression (after the last operator).
     * Removes thousand separators (spaces) for processing.
     *
     * @return Last number as string without spaces
     */
    private String getLastNumber() {
        return expression.toString()
                .replaceAll(".*" + OPERATORS_REGEX, "") // Remove everything before last operator
                .replace(" ", ""); // Remove thousand separators
    }

    /**
     * Replaces the last number in the expression with a new formatted number.
     * Adds thousand separators to the new number.
     *
     * @param newNumber New number to replace the last number
     */
    private void replaceLastNumber(String newNumber) {
        int lastOpIndex = getLastOperatorIndex();
        expression.delete(lastOpIndex, expression.length());
        expression.append(formatNumberWithSpaces(newNumber));
    }

    /**
     * Finds the position after the last operator in the expression.
     * Used to locate where the last number begins.
     *
     * @return Index position after last operator (0 if no operators found)
     */
    private int getLastOperatorIndex() {
        String expr = expression.toString();
        return Math.max(
                Math.max(expr.lastIndexOf('+'), expr.lastIndexOf('-')),
                Math.max(expr.lastIndexOf('*'), expr.lastIndexOf('/'))
        ) + 1;
    }

    /**
     * Handles decimal point input.
     * Adds leading zero if decimal point is at start or after operator.
     * Prevents multiple decimal points in the same number.
     */
    private void handleDecimalPoint() {
        int len = expression.length();

        // Add leading zero if needed
        if (len == 0 || isOperator(expression.charAt(len - 1))) {
            expression.append("0");
        }

        // Only add decimal point if current number doesn't have one
        if (!hasDecimalInCurrentNumber()) {
            expression.append(".");
        }
    }

    /**
     * Checks if the current number (after last operator) already contains a decimal point.
     *
     * @return true if decimal point exists in current number
     */
    private boolean hasDecimalInCurrentNumber() {
        // Scan backwards until we hit an operator or start
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (isOperator(c)) return false;
            if (c == '.') return true;
        }
        return false;
    }

    // ==================== Number formatting methods ====================

    /**
     * Formats a number with thousand separators (spaces every 3 digits).
     * Preserves decimal part if present.
     * Example: "1234567.89" -> "1 234 567.89"
     *
     * @param number Number string to format
     * @return Formatted number with spaces
     */
    private String formatNumberWithSpaces(String number) {
        String[] parts = number.split("\\.");
        String intPart = parts[0];

        // Build integer part with spaces from right to left
        StringBuilder formatted = new StringBuilder();
        for (int i = intPart.length(), count = 0; i > 0; i--, count++) {
            // Add space every 3 digits (but not at the start)
            if (count > 0 && count % 3 == 0) {
                formatted.insert(0, ' ');
            }
            formatted.insert(0, intPart.charAt(i - 1));
        }

        // Append decimal part if exists
        if (parts.length == 2) {
            formatted.append('.').append(parts[1]);
        }

        return formatted.toString();
    }

    /**
     * Formats BigDecimal result for display.
     * Removes trailing zeros, limits decimal places, and adds thousand separators.
     *
     * @param value BigDecimal value to format
     * @return Formatted string representation
     */
    private String formatResult(BigDecimal value) {
        // Remove unnecessary trailing zeros (e.g., 5.00 -> 5)
        value = value.stripTrailingZeros();

        // Limit decimal places for display
        if (value.scale() > DISPLAY_SCALE) {
            value = value.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
        }

        // Split into integer and decimal parts
        String result = value.toPlainString();
        String[] parts = result.split("\\.");

        // Format integer part with thousand separators
        String formatted = formatNumberWithSpaces(parts[0]);

        // Add decimal part if exists
        if (parts.length == 2) {
            formatted += "." + parts[1];
        }

        return formatted;
    }

    // ==================== Expression evaluation methods ====================

    /**
     * Removes all whitespace from expression.
     * Necessary before mathematical evaluation.
     *
     * @param expr Expression with possible spaces
     * @return Expression without spaces
     */
    private String cleanExpression(String expr) {
        return expr.replaceAll("\\s+", "");
    }

    /**
     * Evaluates mathematical expression following order of operations.
     * First processes multiplication and division, then addition and subtraction.
     *
     * @param expr Clean expression string (no spaces)
     * @return Result as BigDecimal
     */
    private BigDecimal evaluateExpression(String expr) {
        // Step 1: Process multiplication and division (higher precedence)
        expr = evaluateByPattern(expr, "([*/])", this::computeMultiplyDivide);

        // Step 2: Process addition and subtraction (lower precedence)
        expr = evaluateByPattern(expr, "([+\\-])", this::computeAddSubtract);

        return new BigDecimal(expr);
    }

    /**
     * Generic method to evaluate operations matching a specific pattern.
     * Repeatedly finds and evaluates operations until none remain.
     *
     * @param expr Expression to evaluate
     * @param opRegex Regular expression for operator(s) to match
     * @param computer Function to compute the operation
     * @return Expression with matched operations evaluated
     */
    private String evaluateByPattern(String expr, String opRegex, OperationComputer computer) {
        // Pattern matches: number operator number
        Pattern pattern = Pattern.compile("(-?\\d+\\.?\\d*)\\s*" + opRegex + "\\s*(-?\\d+\\.?\\d*)");
        Matcher matcher = pattern.matcher(expr);

        // Keep evaluating until no more matches found
        while (matcher.find()) {
            BigDecimal left = new BigDecimal(matcher.group(1));
            String operator = matcher.group(2);
            BigDecimal right = new BigDecimal(matcher.group(3));

            // Compute the operation
            BigDecimal result = computer.compute(left, operator, right);

            // Replace matched portion with result and continue
            expr = expr.substring(0, matcher.start()) + result.toPlainString() + expr.substring(matcher.end());
            matcher = pattern.matcher(expr);
        }

        return expr;
    }

    /**
     * Performs multiplication or division operations.
     *
     * @param left Left operand
     * @param op Operator (* or /)
     * @param right Right operand
     * @return Result of operation
     * @throws ArithmeticException if division by zero
     */
    private BigDecimal computeMultiplyDivide(BigDecimal left, String op, BigDecimal right) {
        if ("*".equals(op)) {
            return left.multiply(right, MC);
        } else {
            // Check for division by zero
            if (right.compareTo(BigDecimal.ZERO) == 0) {
                throw new ArithmeticException("Division by zero");
            }
            return left.divide(right, MC);
        }
    }

    /**
     * Performs addition or subtraction operations.
     *
     * @param left Left operand
     * @param op Operator (+ or -)
     * @param right Right operand
     * @return Result of operation
     */
    private BigDecimal computeAddSubtract(BigDecimal left, String op, BigDecimal right) {
        return "+".equals(op) ? left.add(right, MC) : left.subtract(right, MC);
    }

    // ==================== Character validation methods ====================

    /**
     * Checks if character is a mathematical operator.
     *
     * @param c Character to check
     * @return true if character is +, -, *, or /
     */
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    /**
     * Checks if character is an operator or decimal point.
     *
     * @param c Character to check
     * @return true if character is an operator or decimal point
     */
    private boolean isOperatorOrDot(char c) {
        return isOperator(c) || c == '.';
    }

    /**
     * Functional interface for computing binary operations.
     * Allows passing operation logic as a parameter.
     */
    @FunctionalInterface
    private interface OperationComputer {
        BigDecimal compute(BigDecimal left, String operator, BigDecimal right);
    }
}