package org.example;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.List;

public class MathEvaluatorLibrary {

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
        // Создание выражения с помощью Exp4j
        Expression exp = new ExpressionBuilder(expression).build();
        return exp.evaluate(); // Возвращаем результат
    }
}