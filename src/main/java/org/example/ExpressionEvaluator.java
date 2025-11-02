package org.example;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Evaluates mathematical expressions using Shunting Yard algorithm.
 * More efficient and cleaner than regex-based approach.
 */
class ExpressionEvaluator {
    private static final MathContext MC = new MathContext(34, RoundingMode.HALF_UP);
    private static final Map<Character, Integer> PRECEDENCE = new HashMap<>();

    static {
        PRECEDENCE.put('+', 1);
        PRECEDENCE.put('-', 1);
        PRECEDENCE.put('*', 2);
        PRECEDENCE.put('/', 2);
    }

    /**
     * Evaluates expression using Shunting Yard algorithm + postfix evaluation.
     * This is more efficient than regex matching approach.
     */
    public BigDecimal evaluate(String expression) {
        Deque<BigDecimal> values = new ArrayDeque<>();
        Deque<Character> operators = new ArrayDeque<>();

        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);

            // Skip whitespace
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // Parse number (including negative numbers)
            if (Character.isDigit(c) || c == '.' ||
                    (c == '-' && (i == 0 || isOperator(expression.charAt(i - 1))))) {

                StringBuilder number = new StringBuilder();
                if (c == '-') {
                    number.append(c);
                    i++;
                }

                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    number.append(expression.charAt(i));
                    i++;
                }

                values.push(new BigDecimal(number.toString()));
                continue;
            }

            // Handle operators
            if (isOperator(c)) {
                // Process operators with higher or equal precedence
                while (!operators.isEmpty() &&
                        PRECEDENCE.get(operators.peek()) >= PRECEDENCE.get(c)) {
                    applyOperation(values, operators.pop());
                }
                operators.push(c);
                i++;
            }
        }

        // Process remaining operators
        while (!operators.isEmpty()) {
            applyOperation(values, operators.pop());
        }

        return values.pop();
    }

    /**
     * Applies a binary operation to the top two values in the stack.
     */
    private void applyOperation(Deque<BigDecimal> values, char operator) {
        if (values.size() < 2) {
            throw new IllegalArgumentException("Invalid expression");
        }

        BigDecimal right = values.pop();
        BigDecimal left = values.pop();
        BigDecimal result;

        switch (operator) {
            case '+':
                result = left.add(right, MC);
                break;
            case '-':
                result = left.subtract(right, MC);
                break;
            case '*':
                result = left.multiply(right, MC);
                break;
            case '/':
                if (right.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                result = left.divide(right, MC);
                break;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }

        values.push(result);
    }

    private boolean isOperator(char c) {
        return PRECEDENCE.containsKey(c);
    }
}
