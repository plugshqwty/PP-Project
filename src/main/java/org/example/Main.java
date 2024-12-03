package org.example;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.List;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        FileProcessing processor = new FileProcessing();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Выберите тип файла для чтения: ");
        System.out.println("1. Текстовый файл ");
        System.out.println("2. XML файл ");
        System.out.println("3. JSON файл ");
        System.out.println("4. Yaml файл ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Считываем оставшийся символ новой строки

        switch (choice) {
            case 1:
                Path inputPath1 = Path.of("input.txt"); // Путь к входному файлу
                Path outputPath1 = Path.of("output.txt"); // Путь к выходному файлу

                try {
                    // Чтение выражений из текстового файла
                    List<String> expressions = processor.readTextFile(inputPath1);
                    System.out.println("Прочитанные выражения:");
                    for (String expr : expressions) {
                        System.out.println(expr);
                    }

                    // Запись тех же выражений в другой текстовый файл
                    processor.writeTextFile(outputPath1, expressions);
                    System.out.println("Выражения записаны в " + outputPath1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                Path inputPath2 = Path.of("input.xml"); // Путь к вашему входному XML файлу
                Path outputPath2 = Path.of("output.xml"); // Путь к выходному XML файлу

                try {
                    // Чтение выражений из XML файла
                    MathExamples mathExamples = processor.readXmlFile(inputPath2);
                    System.out.println("Прочитанные выражения из XML:");

                    // Преобразование в List<String>
                    List<String> expressions = new ArrayList<>();
                    for (MathExample example : mathExamples.getExamples()) {
                        expressions.add(example.getExpression());
                    }

                    // Вывод списка строк
                    for (String expr : expressions) {
                        System.out.println(expr);
                    }

                    // Запись тех же выражений в другой XML файл
                    processor.writeXmlFile(outputPath2, mathExamples);
                    System.out.println("Выражения записаны в " + outputPath2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                System.out.println("Выберите цифру от 1 до 4!");
        }

    }
}