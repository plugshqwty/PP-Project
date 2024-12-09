package org.example;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.IOException;
import javax.xml.bind.JAXBException;

public class Main {
    public static void main(String[] args) {
        FileProcessing processor = new FileProcessing();
        FileArchiver archiver = new FileArchiver();
        Scanner scanner = new Scanner(System.in);
        List<String> expressions = new ArrayList<>();

        System.out.println("Выберите источник данных: ");
        System.out.println("1. Обычный файл");
        System.out.println("2. Архивированный файл");
        int sourceChoice = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера

        if (sourceChoice == 1) {
            // Чтение из обычного файла
            System.out.println("Выберите тип файла для чтения: ");
            System.out.println("1. Текстовый файл");
            System.out.println("2. XML файл");
            System.out.println("3. JSON файл");
            System.out.println("4. YAML файл");
            int fileTypeChoice = scanner.nextInt();
            scanner.nextLine();  // Очистка буфера

            Path inputPath = Path.of("input.txt"); // Измените на путь к файлу в зависимости от выбора
            switch (fileTypeChoice) {
                case 1:
                    inputPath = Path.of("input.txt");
                    break;
                case 2:
                    inputPath = Path.of("input.xml");
                    break;
                case 3:
                    inputPath = Path.of("input.json");
                    break;
                case 4:
                    inputPath = Path.of("input.yaml");
                    break;
                default:
                    System.out.println("Неверный выбор.");
                    return;
            }

            try {
                // Чтение выражений в зависимости от типа файла
                if (fileTypeChoice == 1) {
                    expressions = processor.readTextFile(inputPath);
                } else if (fileTypeChoice == 2) {
                    MathExamples mathExamples = processor.readXmlFile(inputPath);
                    for (MathExample example : mathExamples.getExamples()) {
                        expressions.add(example.getExpression());
                    }
                } else if (fileTypeChoice == 3) {
                    MathExampleList mathExampleList = processor.readJsonFile(inputPath);
                    for (MathExample example : mathExampleList.getExamples()) {
                        expressions.add(example.getExpression());
                    }
                } else if (fileTypeChoice == 4) {
                    List<MathExample> mathExamples = processor.readYamlFile(inputPath);
                    for (MathExample example : mathExamples) {
                        expressions.add(example.getExpression());
                    }
                }
            } catch (IOException | JAXBException e) {
                e.printStackTrace();
            }

        } else if (sourceChoice == 2) {
            // Чтение из архива
            Path archivePath = Path.of("input.zip"); // Путь к архиву всегда фиксированный
            Path outputDir = Paths.get("."); // Текущая директория

            System.out.println("Выберите тип файла из архива: ");
            System.out.println("1. Текстовый файл");
            System.out.println("2. XML файл");
            System.out.println("3. JSON файл");
            System.out.println("4. YAML файл");
            int fileTypeChoice = scanner.nextInt();
            scanner.nextLine();  // Очистка буфера

            try {
                // Разархивируем архив
                archiver.extractFile(archivePath, outputDir);

                // Читаем файл из текущей директории
                Path inputPath = outputDir.resolve(fileTypeChoice == 1 ? "input.txt" :
                        fileTypeChoice == 2 ? "input.xml" :
                                fileTypeChoice == 3 ? "input.json" :
                                        "input.yaml");

                // Чтение выражений в зависимости от типа файла
                if (fileTypeChoice == 1) {
                    expressions = processor.readTextFile(inputPath);
                } else if (fileTypeChoice == 2) {
                    MathExamples mathExamples = processor.readXmlFile(inputPath);
                    for (MathExample example : mathExamples.getExamples()) {
                        expressions.add(example.getExpression());
                    }
                } else if (fileTypeChoice == 3) {
                    MathExampleList mathExampleList = processor.readJsonFile(inputPath);
                    for (MathExample example : mathExampleList.getExamples()) {
                        expressions.add(example.getExpression());
                    }
                } else if (fileTypeChoice == 4) {
                    List<MathExample> mathExamples = processor.readYamlFile(inputPath);
                    for (MathExample example : mathExamples) {
                        expressions.add(example.getExpression());
                    }
                }
            } catch (IOException | JAXBException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Неверный выбор источника данных.");
            return;
        }

        // Вывод всех прочитанных выражений
        System.out.println("Прочитанные выражения:");
        for (String expr : expressions) {
            System.out.println(expr);
        }

        // Спрашиваем о типе выходного файла
        System.out.println("Выберите тип выходного файла: ");
        System.out.println("1. Текстовый файл");
        System.out.println("2. XML файл");
        System.out.println("3. JSON файл");
        System.out.println("4. YAML файл");
        int outputTypeChoice = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера

        Path outputPath = Path.of("output"); // Путь к выходному файлу будет определяться ниже
        switch (outputTypeChoice) {
            case 1:
                outputPath = Path.of("output.txt");
                break;
            case 2:
                outputPath = Path.of("output.xml");
                break;
            case 3:
                outputPath = Path.of("output.json");
                break;
            case 4:
                outputPath = Path.of("output.yaml");
                break;
            default:
                System.out.println("Неверный выбор для выходного файла.");
                return;
        }

        // Запись в выходной файл
        try {
            if (outputTypeChoice == 1) {
                processor.writeTextFile(outputPath, expressions);
            } else if (outputTypeChoice == 2) {
                MathExamples mathExamples = new MathExamples();
                List<MathExample> mathExampleList = new ArrayList<>();
                for (String expr : expressions) {
                    MathExample example = new MathExample();
                    example.setExpression(expr);
                    mathExampleList.add(example);
                }
                mathExamples.setExamples(mathExampleList);
                processor.writeXmlFile(outputPath, mathExamples);
            } else if (outputTypeChoice == 3) {
                MathExampleList mathExampleList = new MathExampleList();
                List<MathExample> mathExamples = new ArrayList<>();
                for (String expr : expressions) {
                    MathExample example = new MathExample();
                    example.setExpression(expr);
                    mathExamples.add(example);
                }
                mathExampleList.setExamples(mathExamples);
                processor.writeJsonFile(outputPath, mathExampleList);
            } else if (outputTypeChoice == 4) {
                List<MathExample> mathExamples = new ArrayList<>();
                for (String expr : expressions) {
                    MathExample example = new MathExample();
                    example.setExpression(expr);
                    mathExamples.add(example);
                }
                processor.writeYamlFile(outputPath, mathExamples);
            }
            System.out.println("Выходной файл успешно сохранен: " + outputPath);
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }

        // Спрашиваем, нужно ли архивировать выходной файл
        System.out.println("Хотите архивировать выходной файл? (yes/no)");
        String archiveOutput = scanner.nextLine();
        if (archiveOutput.equalsIgnoreCase("yes")) {
            Path archivePath = Path.of("output.zip");
            try {
                archiver.archiveFile(outputPath, archivePath);
                System.out.println("Выходной файл успешно заархивирован: " + archivePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Выходной файл не был заархивирован.");
        }
    }
}