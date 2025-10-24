package org.example;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Calculator {
    private final StringBuilder expression = new StringBuilder();
    private boolean lastWasResult = false;

    public String handleDigitInput(String input) {
        if (lastWasResult) {
            clear();
        }

        if (".".equals(input)) {
            handleDecimalPoint();
        } else {
            expression.append(input);
        }

        return expression.toString();
    }

    public String handleOperatorInput(String operator) {
        lastWasResult = false;

        if (expression.isEmpty()) {
            if ("-".equals(operator)) {
                expression.append(operator);
            }
            return expression.toString();
        }

        char lastChar = expression.charAt(expression.length() - 1);
        if (isDot(lastChar) || isOperator(lastChar)) {
            expression.deleteCharAt(expression.length() - 1);
        }

        expression.append(operator);
        return expression.toString();
    }

    public String handleOperatorBackspace() {
        lastWasResult = false;

        if (expression.isEmpty()) {
            return expression.toString();
        }

        expression.deleteCharAt(expression.length() - 1);

        return expression.toString();
    }

    public String calculateResult() {
        if (lastWasResult || expression.isEmpty()) {
            return expression.toString();
        }

        // Удаляем оператор в конце, если есть
        char lastChar = expression.charAt(expression.length() - 1);
        if (isOperator(lastChar)) {
            expression.deleteCharAt(expression.length() - 1);
        }

        try {
            Expression exp = new ExpressionBuilder(expression.toString()).build();
            double result = exp.evaluate();

            String formatted = (result % 1 == 0 && !Double.isInfinite(result))
                    ? String.format("%.0f", result)
                    : String.valueOf(result);

            expression.setLength(0);
            expression.append(formatted);
            lastWasResult = true;

            return formatted;
        } catch (Exception e) {
            clear();
            return "Undefined";
        }
    }

    public void clear() {
        expression.setLength(0);
        lastWasResult = false;
    }

    // Вспомогательные методы

    private void handleDecimalPoint() {
        int len = expression.length();

        // Если пусто или последний символ - оператор, добавляем 0
        if (len == 0 || isOperator(expression.charAt(len - 1))) {
            expression.append("0");
        }

        // Проверяем, есть ли точка в текущем числе
        if (!hasDecimalInCurrentNumber()) {
            expression.append(".");
        }
    }

    private boolean hasDecimalInCurrentNumber() {
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (isOperator(c)) {
                return false;
            }
            if (isDot(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean isDot(char c) {
        return c == '.';
    }
}

