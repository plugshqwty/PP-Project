package org.example;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathEvaluatorTests {

    @Test
    public void testMathEvaluator() {
        List<String> expressions = new ArrayList<>(Arrays.asList(
                "3 + 5",
                "10 - 4",
                "6 * 7",
                "8 / 2",
                "25 / 5 - (2 + 1)"
        ));

        MathEvaluator.evaluateExpressions(expressions);

        assertEquals(Arrays.asList("8.0", "6.0", "42.0", "4.0", "2.0"), expressions);
    }

    @Test
    public void testMathEvaluatorLibrary() {
        List<String> expressions = new ArrayList<>(Arrays.asList(
                "3 + 5",
                "10 - 4",
                "6 * 7",
                "8 / 2",
                "25 / 5 - (2 + 1)"
        ));

        MathEvaluatorLibrary.evaluateExpressions(expressions);

        assertEquals(Arrays.asList("8.0", "6.0", "42.0", "4.0", "2.0"), expressions);
    }

    @Test
    public void testMathEvaluatorNot() {
        List<String> expressions = new ArrayList<>(Arrays.asList(
                "3 + 5",
                "10 - 4",
                "6 * 7",
                "8 / 2",
                "25 / 5 - (2 + 1)"
        ));

        MathEvaluatorNot.evaluateExpressions(expressions);

        assertEquals(Arrays.asList("8.0", "6.0", "42.0", "4.0", "2.0"), expressions);
    }

    @Test
    public void testErrorHandling() {
        List<String> expressions = new ArrayList<>(Arrays.asList(
                "3 / 0", // Деление на ноль
                "10 + (2 - 2)", // Обычное выражение
                "invalid expression" // Некорректное выражение
        ));

        MathEvaluator.evaluateExpressions(expressions);
        MathEvaluatorLibrary.evaluateExpressions(expressions);
        MathEvaluatorNot.evaluateExpressions(expressions);

        assertEquals(Arrays.asList("Ошибка", "10.0", "Ошибка"), expressions);
    }
}