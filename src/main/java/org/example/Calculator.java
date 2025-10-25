package org.example;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Бизнес-логика калькулятора с использованием BigDecimal
 */
public class Calculator {
    private static final MathContext MC = new MathContext(34, RoundingMode.HALF_UP);
    private static final int DISPLAY_SCALE = 10;

    private final StringBuilder expression = new StringBuilder();
    private boolean lastWasResult = false;

    /**
     * Обрабатывает ввод цифры или точки
     */
    public String handleDigitInput(String input) {
        if (lastWasResult) {
            clear();
        }

        if (".".equals(input)) {
            handleDecimalPoint();
        } else {
            String expr = expression.toString();
            int lastOpIndex = Math.max(
                    Math.max(expr.lastIndexOf('+'), expr.lastIndexOf('-')),
                    Math.max(expr.lastIndexOf('*'), expr.lastIndexOf('/'))
            );
            lastOpIndex = lastOpIndex == -1 ? 0 : lastOpIndex + 1;

            String lastNumber = expr.replaceAll(".*[+\\-*/]", "");
            if (lastNumber.contains(".") || lastNumber.length() < 3) {
                expression.append(input);
            } else {
                lastNumber = cleanExpression(lastNumber);
                lastNumber = lastNumber + input;

                String formatedLastNumber = addSplittersToNum(lastNumber);

                expression.delete(lastOpIndex, expression.length());
                expression.append(formatedLastNumber);
            }
        }
        return expression.toString();
    }

    private String addSplittersToNum(String expr) {
        StringBuilder tempExp = new StringBuilder();
        for (int i = expr.length(); i >= 0; i = i - 3) {
            if (i > 3) {
                tempExp.insert(0, " " + expr.subSequence(i - 3, i));
            } else {
                tempExp.insert(0, expr.subSequence(0, i));
            }
        }
        return tempExp.toString();

    }

    /**
     * Обрабатывает ввод оператора
     */
    public String handleOperatorInput(String operator) {
        lastWasResult = false;

        if (expression.length() == 0) {
            if ("-".equals(operator)) {
                expression.append(operator);
            }
            return expression.toString();
        }

        char lastChar = expression.charAt(expression.length() - 1);
        if (isOperatorOrDot(lastChar)) {
            expression.deleteCharAt(expression.length() - 1);
        }

        expression.append(operator);
        return expression.toString();
    }

    /**
     * Вычисляет результат выражения используя BigDecimal
     */
    public String calculateResult() {
        if (lastWasResult || expression.length() == 0) {
            return expression.toString();
        }

        // Удаляем оператор в конце, если есть
        char lastChar = expression.charAt(expression.length() - 1);
        if (isOperator(lastChar)) {
            expression.deleteCharAt(expression.length() - 1);
        }

        try {
            BigDecimal result = evaluateExpression(expression.toString());
            String formatted = formatBigDecimal(result);

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
     * Очищает выражение
     */
    public void clear() {
        expression.setLength(0);
        lastWasResult = false;
    }

    // Вспомогательные методы

    private void handleDecimalPoint() {
        int len = expression.length();

        if (len == 0 || isOperator(expression.charAt(len - 1))) {
            expression.append("0");
        }

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
            if (c == '.') {
                return true;
            }
        }
        return false;
    }

    private String cleanExpression(String expr) {
        return expr.replaceAll("\\s+", "");
    }

    /**
     * Вычисляет математическое выражение с использованием BigDecimal
     */
    private BigDecimal evaluateExpression(String expr) {
        // Обрабатываем умножение и деление
        expr = evaluateMultiplicationAndDivision(cleanExpression(expr));
        // Обрабатываем сложение и вычитание
        return evaluateAdditionAndSubtraction(expr);
    }

    private String evaluateMultiplicationAndDivision(String expr) {
        Pattern pattern = Pattern.compile("(-?\\d+\\.?\\d*)\\s*([*/])\\s*(-?\\d+\\.?\\d*)");
        Matcher matcher = pattern.matcher(expr);

        while (matcher.find()) {
            BigDecimal left = new BigDecimal(matcher.group(1));
            String operator = matcher.group(2);
            BigDecimal right = new BigDecimal(matcher.group(3));

            BigDecimal result;
            if ("*".equals(operator)) {
                result = left.multiply(right, MC);
            } else {
                if (right.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                result = left.divide(right, MC);
            }

            expr = expr.substring(0, matcher.start()) + result.toPlainString() + expr.substring(matcher.end());
            matcher = pattern.matcher(expr);
        }

        return expr;
    }

    private BigDecimal evaluateAdditionAndSubtraction(String expr) {
        Pattern pattern = Pattern.compile("(-?\\d+\\.?\\d*)\\s*([+\\-])\\s*(-?\\d+\\.?\\d*)");
        Matcher matcher = pattern.matcher(expr);

        while (matcher.find()) {
            BigDecimal left = new BigDecimal(matcher.group(1));
            String operator = matcher.group(2);
            BigDecimal right = new BigDecimal(matcher.group(3));

            BigDecimal result = "+".equals(operator)
                    ? left.add(right, MC)
                    : left.subtract(right, MC);

            expr = expr.substring(0, matcher.start()) + result.toPlainString() + expr.substring(matcher.end());
            matcher = pattern.matcher(expr);
        }

        return new BigDecimal(expr);
    }

    /**
     * Форматирует BigDecimal для отображения
     */
    private String formatBigDecimal(BigDecimal value) {
        // Удаляем незначащие нули
        value = value.stripTrailingZeros();

        // Если число целое, возвращаем без десятичной точки
        if (value.scale() <= 0) {
            return value.toPlainString();
        }

        // Ограничиваем количество знаков после запятой для отображения
        if (value.scale() > DISPLAY_SCALE) {
            value = value.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
        }

        String strBigDec = value.toPlainString();

        String[] digitsAfterDot = strBigDec.split("\\.");

        if (digitsAfterDot.length == 2){
            strBigDec = addSplittersToNum(digitsAfterDot[0]) + "." + digitsAfterDot[1];
        }else {
            strBigDec = addSplittersToNum(digitsAfterDot[0]);
        }

        return strBigDec;
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean isOperatorOrDot(char c) {
        return isOperator(c) || c == '.';
    }
}