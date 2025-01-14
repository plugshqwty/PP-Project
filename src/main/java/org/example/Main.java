package org.example;

import java.nio.file.*;
import java.util.*;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.crypto.SecretKey;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.ByteArrayInputStream;
import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;

public class Main {
    public static void main(String[] args) {
        FileProcessing processor = new FileProcessing();
        FileArchiver archiver = new FileArchiver();
        Scanner scanner = new Scanner(System.in);
        List<String> expressions = new ArrayList<>();
        SecretKey key = null;

        // Выбор источника данных
        System.out.println("Выберите источник данных: ");
        System.out.println("1. Обычный файл");
        System.out.println("2. Архивированный файл");
        System.out.println("3. Зашифрованный файл");
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
            System.out.println("Выберите тип файлов в архиве: ");
            System.out.println("1. Обычные файлы");
            System.out.println("2. Зашифрованные файлы");
            int enOrNo = scanner.nextInt();
            scanner.nextLine();

            // Чтение из архива
            Path archivePath = Path.of("input.zip"); // Путь к архиву всегда фиксированный
            Path outputDir = Paths.get("."); // Текущая директория

            // Разархивируем архив
            try {
                archiver.extractFile(archivePath, outputDir);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            if (enOrNo == 1) {
                // Обработка обычных файлов
                System.out.println("Выберите тип файла из архива: ");
                System.out.println("1. Текстовый файл");
                System.out.println("2. XML файл");
                System.out.println("3. JSON файл");
                System.out.println("4. YAML файл");
                int fileTypeChoice = scanner.nextInt();
                scanner.nextLine();  // Очистка буфера

                Path inputPath = outputDir.resolve(fileTypeChoice == 1 ? "input.txt" :
                        fileTypeChoice == 2 ? "input.xml" :
                                fileTypeChoice == 3 ? "input.json" :
                                        "input.yaml");

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
            } else if (enOrNo == 2) {
                // Обработка зашифрованных файлов
                System.out.println("Выберите тип зашифрованного файла для чтения: ");
                System.out.println("1. Текстовый файл (.enc)");
                System.out.println("2. XML файл (.enc)");
                System.out.println("3. JSON файл (.enc)");
                System.out.println("4. YAML файл (.enc)");
                int fileTypeChoice = scanner.nextInt();
                scanner.nextLine();  // Очистка буфера

                Path encryptedPath;
                Path keyPath;
                Path ivPath;

                switch (fileTypeChoice) {
                    case 1:
                        encryptedPath = outputDir.resolve("input.txt.enc");
                        keyPath = Paths.get("keyfiletxt.key");
                        ivPath = Paths.get("ivfiletxt.bin");
                        break;
                    case 2:
                        encryptedPath = outputDir.resolve("input.xml.enc");
                        keyPath = Paths.get("keyfilexml.key");
                        ivPath = Paths.get("ivfilexml.bin");
                        break;
                    case 3:
                        encryptedPath = outputDir.resolve("input.json.enc");
                        keyPath = Paths.get("keyfilejson.key");
                        ivPath = Paths.get("ivfilejson.bin");
                        break;
                    case 4:
                        encryptedPath = outputDir.resolve("input.yaml.enc");
                        keyPath = Paths.get("keyfileyaml.key");
                        ivPath = Paths.get("ivfileyaml.bin");
                        break;
                    default:
                        System.out.println("Неверный выбор.");
                        return;
                }

                try {
                    key = FileEncryptor.loadKey(keyPath);
                    byte[] iv = FileEncryptor.loadIv(ivPath);
                    // Расшифровка файла
                    Path decryptedPath = Path.of("decrypted_output.txt"); // Путь для сохранения расшифрованного файла
                    FileEncryptor.decryptFile(encryptedPath, decryptedPath, key, iv);

                    // Чтение расшифрованного содержимого
                    String decryptedContent = new String(Files.readAllBytes(decryptedPath));

                    // Извлечение математических выражений
                    expressions = extractMathExpressions(decryptedContent, fileTypeChoice);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Неверный выбор.");
                return;
            }
        } else if (sourceChoice == 3) {
            // Чтение из зашифрованного файла
            System.out.println("Выберите тип зашифрованного файла для чтения: ");
            System.out.println("1. Текстовый файл (.enc)");
            System.out.println("2. XML файл (.enc)");
            System.out.println("3. JSON файл (.enc)");
            System.out.println("4. YAML файл (.enc)");
            int fileTypeChoice = scanner.nextInt();
            scanner.nextLine();  // Очистка буфера
            Path keyPath;
            Path ivPath;
            Path encryptedPath = Path.of("input"); // Путь к зашифрованному файлу
            switch (fileTypeChoice) {
                case 1:
                    encryptedPath = Path.of("input.txt.enc");
                    keyPath = Paths.get("keyfiletxt.key");
                    ivPath = Paths.get("ivfiletxt.bin");
                    break;
                case 2:
                    encryptedPath = Path.of("input.xml.enc");
                    keyPath = Paths.get("keyfilexml.key");
                    ivPath = Paths.get("ivfilexml.bin");
                    break;
                case 3:
                    encryptedPath = Path.of("input.json.enc");
                    keyPath = Paths.get("keyfilejson.key");
                    ivPath = Paths.get("ivfilejson.bin");
                    break;
                case 4:
                    encryptedPath = Path.of("input.yaml.enc");
                    keyPath = Paths.get("keyfileyaml.key");
                    ivPath = Paths.get("ivfileyaml.bin");
                    break;
                default:
                    System.out.println("Неверный выбор.");
                    return;
            }

            try {
                key = FileEncryptor.loadKey(keyPath);
                byte[] iv = FileEncryptor.loadIv(ivPath);
                // Расшифровка файла
                Path decryptedPath = Path.of("decrypted_output.txt"); // Путь для сохранения расшифрованного файла
                FileEncryptor.decryptFile(encryptedPath, decryptedPath, key, iv);

                // Чтение расшифрованного содержимого
                String decryptedContent = new String(Files.readAllBytes(decryptedPath));

                // Извлечение математических выражений
                expressions = extractMathExpressions(decryptedContent, fileTypeChoice);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Неверный выбор источника данных.");
            return;
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////
        // Вывод всех прочитанных выражений
        System.out.println("Прочитанные выражения:");
        for (String expr : expressions) {
            System.out.println(expr);
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("Выберите способ подсчета выражений: ");
        System.out.println("1. Используя регулярные выражения");
        System.out.println("2. Без использования регулярных выражений");
        int сhoice = scanner.nextInt();
        scanner.nextLine();

        switch (сhoice) {
            case 1:
                MathEvaluator.evaluateExpressions(expressions);
                break;
            case 2:
                MathEvaluatorNot.evaluateExpressions(expressions);
                break;
            default:
                System.out.println("Неверный выбор.");
                return;
        }



        // Спрашиваем о типе выходного файла
        System.out.println("Выберите тип выходного файла: ");
        System.out.println("1. Текстовый файл");
        System.out.println("2. XML файл");
        System.out.println("3. JSON файл");
        System.out.println("4. YAML файл");
        int outputTypeChoice = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера

        Path outputPath = Path.of("output"); // Путь к выходному файлу
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
                processor.writeYamlFile(outputPath, expressions);
            }
            System.out.println("Выходной файл успешно сохранен: " + outputPath);
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }

        // Шифрование выходного файла
        System.out.println("Хотите зашифровать выходной файл? (yes/no)");
        String encryptOutput = scanner.nextLine();
        boolean isEncrypted = false; // Переменная для отслеживания, был ли файл зашифрован
        if (encryptOutput.equalsIgnoreCase("yes")) {
            try {
                key = FileEncryptor.generateKey(); // Генерация нового ключа
                Path keyPath = Path.of("keyfile.key");
                FileEncryptor.saveKey(key, keyPath); // Сохраняем ключ
                byte[] iv = FileEncryptor.generateIv(); // Генерация IV
                Path ivPath = Path.of("ivfile.bin");
                FileEncryptor.saveIv(iv, ivPath); // Сохраняем IV
                Path encryptedOutputPath = Path.of(outputPath.toString() + ".enc");
                FileEncryptor.encryptFile(outputPath, encryptedOutputPath, key, iv);
                System.out.println("Выходной файл успешно зашифрован: " + encryptedOutputPath);
                outputPath = encryptedOutputPath; // Обновляем путь на зашифрованный файл
                isEncrypted = true; // Устанавливаем флаг, что файл зашифрован
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    // Метод для извлечения математических выражений в зависимости от формата
    private static List<String> extractMathExpressions(String content, int fileType) {
        List<String> mathExpressions = new ArrayList<>();
        switch (fileType) {
            case 2: // XML
                mathExpressions = extractFromXml(content);
                break;
            case 3: // JSON
                mathExpressions = extractFromJson(content);
                break;
            case 4: // YAML
                mathExpressions = extractFromYaml(content);
                break;
            default:
                String[] lines = content.split("\\r?\\n");
                for (String line : lines) {
                    if (isValidMathExpression(line)) {
                        mathExpressions.add(line.trim());
                    }
                }
                break;
        }
        return mathExpressions;
    }

    // Извлечение выражений из XML
    private static List<String> extractFromXml(String xmlContent) {
        List<String> expressions = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));
            NodeList nodeList = document.getElementsByTagName("expression");
            for (int i = 0; i < nodeList.getLength(); i++) {
                expressions.add(nodeList.item(i).getTextContent().trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return expressions;
    }

    // Извлечение выражений из JSON
    private static List<String> extractFromJson(String jsonContent) {
        List<String> expressions = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonContent);
            JsonNode examplesNode = rootNode.get("examples");

            if (examplesNode != null && examplesNode.isArray()) {
                for (JsonNode node : examplesNode) {
                    JsonNode expressionNode = node.get("expression");
                    if (expressionNode != null) {
                        String expression = expressionNode.asText().trim();
                        if (isValidMathExpression(expression)) {
                            expressions.add(expression);
                        } else {
                            System.out.println("Найдено невалидное математическое выражение: " + expression);
                        }
                    } else {
                        System.out.println("Ключ 'expression' отсутствует в одном из объектов.");
                    }
                }
            } else {
                System.out.println("Ключ 'examples' отсутствует или не является массивом.");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при извлечении из JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return expressions;
    }
    // Извлечение выражений из YAML
    private static List<String> extractFromYaml(String yamlContent) {
        List<String> expressions = new ArrayList<>();
        Yaml yaml = new Yaml();

        // Загружаем данные как List<Map<String, String>>
        List<Map<String, String>> data = yaml.load(yamlContent);

        for (Map<String, String> entry : data) {
            String expression = entry.get("expression");
            if (expression != null) {
                expressions.add(expression.trim());
            } else {
                System.out.println("Ключ 'expression' отсутствует в одном из объектов.");
            }
        }
        return expressions;
    }
    // Метод для проверки, является ли строка математическим выражением
    private static boolean isValidMathExpression(String expr) {
        return expr.matches(".*[0-9].*"); // Пример проверки: содержит цифры
    }

}