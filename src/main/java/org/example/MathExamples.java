package org.example;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "mathExamples")
public class MathExamples {

    private List<MathExample> examples;

    @XmlElement(name = "example")
    public List<MathExample> getExamples() {
        return examples;
    }

    public void setExamples(List<MathExample> examples) {
        this.examples = examples;
    }

    // Метод для преобразования в строку
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (MathExample example : examples) {
            result.append(example.getExpression()).append("\n");
        }
        return result.toString();
    }
}