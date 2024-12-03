package org.example;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.util.Scanner;
import java.io.IOException;



public class Main {
    public static void main(String[] args) {
        FileProcessing processor = new FileProcessing();
        Scanner scanner = new Scanner(System.in);
        List<String> expressions = new ArrayList<>();

        System.out.println("Выберите тип файла для чтения: ");
        System.out.println("1. Текстовый файл ");
        System.out.println("2. XML файл ");
        System.out.println("3. JSON файл ");
        System.out.println("4. Yaml файл ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1: {
                Path inputPath1 = Path.of("input.txt");
                Path outputPath1 = Path.of("output.txt");

                try {
                    // Чтение выражений из текстового файла
                    expressions = processor.readTextFile(inputPath1);
                   // System.out.println("Прочитанные выражения записаны в " + outputPath1);

                    //        !!!Запись тех же выражений в другой текстовый файл
                    // processor.writeTextFile(outputPath1, expressions);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 2: {
                Path inputPath2 = Path.of("input.xml");
                Path outputPath2 = Path.of("output.xml");

                try {
                    // Чтение выражений из XML файла
                    MathExamples mathExamples = processor.readXmlFile(inputPath2);

                    // Преобразование в List<String>
                    for (MathExample example : mathExamples.getExamples()) {
                        expressions.add(example.getExpression());
                    }

                    //     !!!! Запись тех же выражений в другой XML файл
                   // processor.writeXmlFile(outputPath2, mathExamples);
                    //System.out.println("Прочитанные выражения записаны в " + outputPath2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case 3: {
                Path inputPath3 = Path.of("input.json");
                Path outputPath3 = Path.of("output.json");
                try {
                    // Чтение выражений из JSON файла
                    MathExampleList mathExampleList = processor.readJsonFile(inputPath3);

                    // Преобразование в List<String>
                    for (MathExample example : mathExampleList.getExamples()) {
                        expressions.add(example.getExpression());
                    }

                    //     !!!!!!!!Запись тех же выражений в другой JSON файл
                   // processor.writeJsonFile(outputPath3, mathExampleList);
                    //System.out.println("Прочитанные выражения записаны в " + outputPath3);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case 4: { // Обработка случая для YAML
                Path inputPath4 = Path.of("input.yaml");
                Path outputPath4 = Path.of("output.yaml");

                try {
                    // Чтение выражений из YAML файла
                    List<MathExample> mathExamples = processor.readYamlFile(inputPath4);

                    // Преобразование в List<String>
                    for (MathExample example : mathExamples) {
                        expressions.add(example.getExpression());
                    }

                    //      !!!!!!Запись тех же выражений в другой YAML файл
                    //processor.writeYamlFile(outputPath4, mathExamples);
                    //System.out.println("Прочитанные выражения записаны в " + outputPath4);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            default:
                System.out.println("Выберите цифру от 1 до 4!");
        }


        System.out.println("Вывод всех прочитанных выражений:");
        for (String expr : expressions) {
            System.out.println(expr);
        }
    }
}
