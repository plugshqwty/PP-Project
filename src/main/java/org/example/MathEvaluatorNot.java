package org.example;

import java.util.ArrayList;
import java.util.List;

public class MathEvaluatorNot {

    public static List<String> evaluateExpressions(List<String> expressions) {
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
        return expressions;
    }

    private static double evaluateExpression(String expression) throws Exception {
        // Удаление пробелов
        expression = expression.replaceAll(" ", "");

        // Обработка скобок
        while (expression.contains("(")) {
            int start = expression.lastIndexOf("(");
            int end = expression.indexOf(")", start);
            if (end == -1) throw new Exception("Несоответствующая скобка");
            String innerExpression = expression.substring(start + 1, end);
            double innerResult = evaluateSimpleExpression(innerExpression);
            expression = expression.substring(0, start) + innerResult + expression.substring(end + 1);
        }

        return evaluateSimpleExpression(expression);
    }

    private static double evaluateSimpleExpression(String expression) throws Exception {
        List<Double> numbers = new ArrayList<>();
        List<Character> operators = new ArrayList<>();

        // Парсинг выражения
        StringBuilder numberBuffer = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);

            if (Character.isDigit(currentChar) || currentChar == '.') {
                numberBuffer.append(currentChar);
            } else {
                if (numberBuffer.length() > 0) {
                    numbers.add(Double.parseDouble(numberBuffer.toString()));
                    numberBuffer.setLength(0);
                }
                if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/') {
                    operators.add(currentChar);
                }
            }
        }

        // Добавляем последнее число
        if (numberBuffer.length() > 0) {
            numbers.add(Double.parseDouble(numberBuffer.toString()));
        }

        // Обработка умножения и деления
        for (int i = 0; i < operators.size(); i++) {
            char operator = operators.get(i);
            if (operator == '*' || operator == '/') {
                double num1 = numbers.get(i);
                double num2 = numbers.get(i + 1);
                double result;

                if (operator == '*') {
                    result = num1 * num2;
                } else {
                    if (num2 == 0) throw new ArithmeticException("Деление на ноль");
                    result = num1 / num2;
                }

                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
                i--; // Корректируем индекс
            }
        }

        // Обработка сложения и вычитания
        double result = numbers.get(0);
        for (int i = 0; i < operators.size(); i++) {
            char operator = operators.get(i);
            double num = numbers.get(i + 1);

            if (operator == '+') {
                result += num;
            } else if (operator == '-') {
                result -= num;
            }
        }

        return result;
    }
}