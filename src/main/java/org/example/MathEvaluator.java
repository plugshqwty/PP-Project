package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathEvaluator {

    public static void evaluateExpressions(List<String> expressions) {
        for (int i = 0; i < expressions.size(); i++) {
            String expression = expressions.get(i);
            try {
                double result = evaluateExpression(expression);
                expressions.set(i, Double.toString(result)); // Заменяем выражение на результат
            } catch (Exception e) {
                expressions.set(i, "Ошибка"); // Или любое другое сообщение об ошибке
            }
        }

        // Вывод результатов
        expressions.forEach(System.out::println);
    }

    private static double evaluateExpression(String expression) throws Exception {
        // Удаление пробелов
        expression = expression.replaceAll(" ", "");

        // Обработка скобок
        while (expression.contains("(")) {
            Matcher matcher = Pattern.compile("\\(([^()]+)\\)").matcher(expression);
            if (matcher.find()) {
                String innerExpression = matcher.group(1);
                double innerResult = evaluateSimpleExpression(innerExpression);
                expression = matcher.replaceFirst(Double.toString(innerResult));
            }
        }

        return evaluateSimpleExpression(expression);
    }

    private static double evaluateSimpleExpression(String expression) throws Exception {
        // Сначала обрабатываем умножение и деление
        Pattern pattern = Pattern.compile("(-?\\d+(?:\\.\\d+)?)([*/])(-?\\d+(?:\\.\\d+)?)");
        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            double num1 = Double.parseDouble(matcher.group(1));
            String operator = matcher.group(2);
            double num2 = Double.parseDouble(matcher.group(3));
            double result;

            switch (operator) {
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 == 0) throw new ArithmeticException("Деление на ноль");
                    result = num1 / num2;
                    break;
                default:
                    throw new Exception("Неизвестный оператор");
            }

            // Заменяем выражение на результат
            expression = matcher.replaceFirst(Double.toString(result));
            matcher = pattern.matcher(expression); // Обновляем matcher
        }

        // Затем обрабатываем сложение и вычитание
        pattern = Pattern.compile("(-?\\d+(?:\\.\\d+)?)([+-])(-?\\d+(?:\\.\\d+)?)");
        matcher = pattern.matcher(expression);

        while (matcher.find()) {
            double num1 = Double.parseDouble(matcher.group(1));
            String operator = matcher.group(2);
            double num2 = Double.parseDouble(matcher.group(3));
            double result;

            switch (operator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                default:
                    throw new Exception("Неизвестный оператор");
            }

            // Заменяем выражение на результат
            expression = matcher.replaceFirst(Double.toString(result));
            matcher = pattern.matcher(expression); // Обновляем matcher
        }

        // Если осталась только одна цифра
        return Double.parseDouble(expression);
    }
}