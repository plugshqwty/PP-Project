package org.example;

import java.util.List;

public class MathExampleList {
    private List<MathExample> examples; // Убираем аннотацию

    public List<MathExample> getExamples() {
        return examples;
    }

    public void setExamples(List<MathExample> examples) {
        this.examples = examples;
    }
}