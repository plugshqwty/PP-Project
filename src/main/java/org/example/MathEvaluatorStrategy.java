package org.example;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathEvaluatorStrategy {

    // Интерфейс для стратегий
    public interface MathEvaluationStrategy {
        List<String> evaluateExpressions(List<String> expressions);
    }

    // Реализация для MathEvaluator
    public static class MathEvaluator implements MathEvaluationStrategy {
        @Override
        public List<String> evaluateExpressions(List<String> expressions) {
            for (int i = 0; i < expressions.size(); i++) {
                String expression = expressions.get(i);
                try {
                    double result = evaluateExpression(expression);
                    expressions.set(i, Double.toString(result));
                } catch (Exception e) {
                    expressions.set(i, "Ошибка");
                }
            }
            return expressions;
        }

        private double evaluateExpression(String expression) throws Exception {
            expression = expression.replaceAll(" ", "");
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

        private double evaluateSimpleExpression(String expression) throws Exception {
            // Удаление пробелов
            expression = expression.replaceAll(" ", "");

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

            // Если осталась только одна цифра, возвращаем её
            return Double.parseDouble(expression);
        }
    }

    // Реализация для MathEvaluatorNot
    public static class MathEvaluatorNot implements MathEvaluationStrategy {
        @Override
        public List<String> evaluateExpressions(List<String> expressions) {
            for (int i = 0; i < expressions.size(); i++) {
                String expression = expressions.get(i);
                try {
                    double result = evaluateExpression(expression);
                    expressions.set(i, Double.toString(result));
                } catch (Exception e) {
                    expressions.set(i, "Ошибка");
                }
            }
            return expressions;
        }

        private double evaluateExpression(String expression) throws Exception {
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

        private double evaluateSimpleExpression(String expression) throws Exception {
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

    // Реализация для MathEvaluatorLibrary
    public static class MathEvaluatorLibrary implements MathEvaluationStrategy {
        @Override
        public List<String> evaluateExpressions(List<String> expressions) {
            for (int i = 0; i < expressions.size(); i++) {
                String expression = expressions.get(i);
                try {
                    double result = evaluateExpression(expression);
                    expressions.set(i, Double.toString(result));
                } catch (Exception e) {
                    expressions.set(i, "Ошибка");
                }
            }
            return expressions;
        }

        private double evaluateExpression(String expression) throws Exception {
            Expression exp = new ExpressionBuilder(expression).build();
            return exp.evaluate();
        }
    }

    // Контекст для использования стратегий
    public static class MathEvaluatorContext {
        private MathEvaluationStrategy strategy;

        public void setStrategy(MathEvaluationStrategy strategy) {
            this.strategy = strategy;
        }

        public List<String> evaluateExpressions(List<String> expressions) {
            return strategy.evaluateExpressions(expressions);
        }
    }
}