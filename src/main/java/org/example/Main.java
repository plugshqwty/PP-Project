package org.example;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.yaml.snakeyaml.Yaml;

import javax.crypto.SecretKey;
import javax.swing.*;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    private static List<String> expressions; // Пустой лист для выражений

    public static void main(String[] args) {
        // Инициализация списка выражений
        expressions = new ArrayList<>();

        // Создание и отображение GUI
        SwingUtilities.invokeLater(() -> new MathEvaluatorGUI());
    }

    static class MathEvaluatorGUI {
        private JFrame frame;
        private JTextArea textArea;
        private DefaultListModel<String> listModel; // Модель для JList
        private JList<String> expressionList; // Список выражений

        public MathEvaluatorGUI() {
            createAndShowGUI();
        }

        private void createAndShowGUI() {
            FileProcessing processor = new FileProcessing();
            FileArchiver archiver = new FileArchiver();

            final SecretKey[] key = {null};
            frame = new JFrame("Math Evaluator");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 650);
            frame.getContentPane().setBackground(hexToColor("#FF69B4")); // Установка фона
            frame.add(new JLabel("Тестирование фона", SwingConstants.CENTER)); // Добавляем простой компонент

            Dimension buttonSize = new Dimension(400, 50); // Установите желаемый размер кнопок
            Font buttonFont = new Font("Arial", Font.BOLD, 15); // Установите шрифт для кнопок

            // Создаем текстовое поле
            textArea = new JTextArea();
            textArea.setEditable(false);
            // textArea.setFont(new Font("Arial", Font.PLAIN, 36));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300)); // Устанавливаем размеры для JScrollPane


            // Создаем модель и список для отображения выражений
            listModel = new DefaultListModel<>();
            expressionList = new JList<>(listModel);
            JScrollPane listScrollPane = new JScrollPane(expressionList);

            // Создаем панель для кнопок
            JPanel panel = new JPanel();
            panel.setBackground(hexToColor("#CFD8D7"));
            expressionList.setFont(new Font("Arial", Font.BOLD, 24));
            listScrollPane.setPreferredSize(new Dimension(400, 300));
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Расположение кнопок в столбик
            panel.setAlignmentX(Component.RIGHT_ALIGNMENT); // Выравнивание вправо



            // Кнопка "Обычный файл" с выпадающим меню
            JButton normalFileButton = new JButton("Обычный файл");
            // Кнопка "Обычный файл"
            normalFileButton.setPreferredSize(buttonSize);
            normalFileButton.setFont(buttonFont);
            JPopupMenu normalFileMenu = new JPopupMenu();
            String[] normalFileTypes = {"Текстовый файл", "XML файл", "JSON файл", "YAML файл"};
            for (String fileType : normalFileTypes) {
                JMenuItem item = new JMenuItem(fileType);
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {


                        if (fileType.equals("Текстовый файл")) {
                            Path inputPath = Path.of("input.txt");
                            try {
                                expressions = processor.readTextFile(inputPath);
                                updateExpressionList(); // Обновляем список
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        } else if(fileType.equals("XML файл")){
                            Path inputPath = Path.of("input.xml");
                            MathExamples mathExamples = null;
                            try {
                                mathExamples = processor.readXmlFile(inputPath);
                            } catch (JAXBException ex) {
                                throw new RuntimeException(ex);
                            }
                            for (MathExample example : mathExamples.getExamples()) {
                                expressions.add(example.getExpression());
                            }
                            updateExpressionList(); // Обновляем список
                        } else if(fileType.equals("JSON файл")){
                            Path inputPath = Path.of("input.json");
                            MathExampleList mathExampleList = null;
                            try {
                                mathExampleList = processor.readJsonFile(inputPath);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            for (MathExample example : mathExampleList.getExamples()) {
                                expressions.add(example.getExpression());
                            }
                            updateExpressionList(); // Обновляем список
                        } else if(fileType.equals("YAML файл")){
                            Path inputPath = Path.of("input.yaml");
                            List<MathExample> mathExamples = null;
                            try {
                                mathExamples = processor.readYamlFile(inputPath);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            for (MathExample example : mathExamples) {
                                expressions.add(example.getExpression());
                            }
                            updateExpressionList(); // Обновляем список
                        }
                        System.out.println("Выбран нормальный файл: " + fileType);
                    }
                });
                normalFileMenu.add(item);
            }

            normalFileButton.addActionListener(e -> normalFileMenu.show(normalFileButton, 0, normalFileButton.getHeight()));
            panel.add(normalFileButton);

            // Кнопка "Заархивированный файл" с выпадающим меню
            JButton archivedFileButton = new JButton("Заархивированный файл");

            // Кнопка "Заархивированный файл"
            archivedFileButton.setPreferredSize(buttonSize);
            archivedFileButton.setFont(buttonFont);

            JPopupMenu archivedFileMenu = new JPopupMenu();
            String[] archivedFileOptions = {
                    "Обычные файлы", "Зашифрованные файлы"
            };

            for (String fileOption : archivedFileOptions) {
                JMenuItem item = new JMenuItem(fileOption);
                item.addActionListener(e -> {
                    if (fileOption.equals("Обычные файлы")) {
                        // Обработка обычных файлов
                        String[] fileTypes = {"Текстовый файл", "XML файл", "JSON файл", "YAML файл"};
                        String fileType = (String) JOptionPane.showInputDialog(
                                null,
                                "Выберите тип файла:",
                                "Тип файла",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                fileTypes,
                                fileTypes[0]
                        );
                        Path archivePath = Path.of("input.zip"); // Путь к архиву всегда фиксированный
                        Path outputDir = Paths.get("."); // Текущая директория

                        // Разархивируем архив
                        try {
                            archiver.extractFile(archivePath, outputDir);
                        } catch (IOException a) {
                            a.printStackTrace();
                        }
                        if (fileType != null) {
                            Path inputPath = outputDir.resolve(
                                    fileType.equals("Текстовый файл") ? "input.txt" :
                                            fileType.equals("XML файл") ? "input.xml" :
                                                    fileType.equals("JSON файл") ? "input.json" :
                                                            "input.yaml"
                            );

                            try {
                                // Чтение выражений в зависимости от типа файла
                                if (fileType.equals("Текстовый файл")) {

                                    expressions = processor.readTextFile(inputPath);
                                    updateExpressionList();
                                } else if (fileType.equals("XML файл")) {
                                    MathExamples mathExamples = processor.readXmlFile(inputPath);
                                    for (MathExample example : mathExamples.getExamples()) {
                                        expressions.add(example.getExpression());
                                    }
                                    updateExpressionList();
                                } else if (fileType.equals("JSON файл")) {
                                    MathExampleList mathExampleList = processor.readJsonFile(inputPath);
                                    for (MathExample example : mathExampleList.getExamples()) {
                                        expressions.add(example.getExpression());
                                    }
                                    updateExpressionList();
                                } else if (fileType.equals("YAML файл")) {
                                    List<MathExample> mathExamples = processor.readYamlFile(inputPath);
                                    for (MathExample example : mathExamples) {
                                        expressions.add(example.getExpression());
                                    }
                                    updateExpressionList();
                                }
                                //updateExpressionList(); // Обновляем список после загрузки
                            } catch (IOException | JAXBException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } else if (fileOption.equals("Зашифрованные файлы")) {
                        // Обработка зашифрованных файлов
                        String[] encryptedFileTypes = {"Текстовый файл (.enc)", "XML файл (.enc)", "JSON файл (.enc)", "YAML файл (.enc)"};
                        String encryptedFileType = (String) JOptionPane.showInputDialog(
                                null,
                                "Выберите тип зашифрованного файла:",
                                "Тип зашифрованного файла",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                encryptedFileTypes,
                                encryptedFileTypes[0]
                        );
                        Path archivePath = Path.of("input.zip"); // Путь к архиву всегда фиксированный
                        Path outputDir = Paths.get("."); // Текущая директория

                        // Разархивируем архив
                        try {
                            archiver.extractFile(archivePath, outputDir);
                        } catch (IOException a) {
                            a.printStackTrace();
                        }
                        if (encryptedFileType != null) {
                            Path encryptedPath;
                            Path keyPath;
                            Path ivPath;

                            // Определяем пути к зашифрованным файлам и ключам
                            int filetype;
                            switch (encryptedFileType) {
                                case "Текстовый файл (.enc)":
                                    filetype=1;
                                    encryptedPath = outputDir.resolve("input.txt.enc");
                                    keyPath = Paths.get("keyfiletxt.key");
                                    ivPath = Paths.get("ivfiletxt.bin");
                                    break;
                                case "XML файл (.enc)":
                                    filetype=2;
                                    encryptedPath = outputDir.resolve("input.xml.enc");
                                    keyPath = Paths.get("keyfilexml.key");
                                    ivPath = Paths.get("ivfilexml.bin");
                                    break;
                                case "JSON файл (.enc)":
                                    filetype=3;
                                    encryptedPath = outputDir.resolve("input.json.enc");
                                    keyPath = Paths.get("keyfilejson.key");
                                    ivPath = Paths.get("ivfilejson.bin");
                                    break;
                                case "YAML файл (.enc)":
                                    filetype=4;
                                    encryptedPath = outputDir.resolve("input.yaml.enc");
                                    keyPath = Paths.get("keyfileyaml.key");
                                    ivPath = Paths.get("ivfileyaml.bin");
                                    break;
                                default:
                                    JOptionPane.showMessageDialog(null, "Неверный выбор.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                                    return;
                            }

                            try {
                                SecretKey secretKey = FileEncryptor.loadKey(keyPath); // Изменяем имя переменной
                                byte[] iv = FileEncryptor.loadIv(ivPath);
                                // Расшифровка файла
                                Path decryptedPath = outputDir.resolve("decrypted_output.txt"); // Путь для сохранения расшифрованного файла
                                FileEncryptor.decryptFile(encryptedPath, decryptedPath, secretKey, iv);

                                // Чтение расшифрованного содержимого
                                String decryptedContent = new String(Files.readAllBytes(decryptedPath));
                                expressions = extractMathExpressions(decryptedContent, filetype); // Передаем '0' или нужный тип
                                updateExpressionList(); // Обновляем список после расшифровки
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
                archivedFileMenu.add(item);
            }

            archivedFileButton.addActionListener(e -> archivedFileMenu.show(archivedFileButton, 0, archivedFileButton.getHeight()));
            panel.add(archivedFileButton);

            // Кнопка "Зашифрованный файл" с выпадающим меню
            JButton encryptedFileButton = new JButton("Зашифрованный файл");
            // Кнопка "Зашифрованный файл"
            encryptedFileButton.setPreferredSize(buttonSize);
            encryptedFileButton.setFont(buttonFont);

            JPopupMenu encryptedFileMenu = new JPopupMenu();
            String[] encryptedFileTypes = {"Текстовый файл", "XML файл", "JSON файл", "YAML файл"};
            for (String fileType : encryptedFileTypes) {
                JMenuItem item = new JMenuItem(fileType);
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Логика для обработки зашифрованного файла

                        if (fileType != null) {
                            Path outputDir = Paths.get("."); // Текущая директория
                            Path encryptedPath;
                            Path keyPath;
                            Path ivPath;

                            // Определяем пути к зашифрованным файлам и ключам
                            int filetype;
                            switch (fileType) {
                                case "Текстовый файл":
                                    filetype = 1;
                                    encryptedPath = outputDir.resolve("input.txt.enc");
                                    keyPath = Paths.get("keyfiletxt.key");
                                    ivPath = Paths.get("ivfiletxt.bin");
                                    break;
                                case "XML файл":
                                    filetype = 2;
                                    encryptedPath = outputDir.resolve("input.xml.enc");
                                    keyPath = Paths.get("keyfilexml.key");
                                    ivPath = Paths.get("ivfilexml.bin");
                                    break;
                                case "JSON файл":
                                    filetype = 3;
                                    encryptedPath = outputDir.resolve("input.json.enc");
                                    keyPath = Paths.get("keyfilejson.key");
                                    ivPath = Paths.get("ivfilejson.bin");
                                    break;
                                case "YAML файл":
                                    filetype = 4;
                                    encryptedPath = outputDir.resolve("input.yaml.enc");
                                    keyPath = Paths.get("keyfileyaml.key");
                                    ivPath = Paths.get("ivfileyaml.bin");
                                    break;
                                default:
                                    JOptionPane.showMessageDialog(null, "Неверный выбор.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                                    return;
                            }
                            try {
                                SecretKey secretKey = FileEncryptor.loadKey(keyPath); // Изменяем имя переменной
                                byte[] iv = FileEncryptor.loadIv(ivPath);
                                // Расшифровка файла
                                Path decryptedPath = outputDir.resolve("decrypted_output.txt"); // Путь для сохранения расшифрованного файла
                                FileEncryptor.decryptFile(encryptedPath, decryptedPath, secretKey, iv);

                                // Чтение расшифрованного содержимого
                                String decryptedContent = new String(Files.readAllBytes(decryptedPath));
                                expressions = extractMathExpressions(decryptedContent, filetype); // Передаем '0' или нужный тип
                                updateExpressionList(); // Обновляем список после расшифровки
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
                encryptedFileMenu.add(item);
            }
            encryptedFileButton.addActionListener(e -> encryptedFileMenu.show(encryptedFileButton, 0, encryptedFileButton.getHeight()));
            panel.add(encryptedFileButton);


            JButton countingButton = new JButton("Вычислить");
            // Кнопка "Вычислить"
            countingButton.setPreferredSize(buttonSize);
            countingButton.setFont(buttonFont);

            JPopupMenu countingMenu = new JPopupMenu();
            String[] countingTypes = {
                    "Используя регулярные выражения",
                    "Без использования регулярных выражений",
                    "Используя библиотеку"
            };
            List <String> results = new ArrayList<>();
            for (String countType : countingTypes) {
                JMenuItem item1 = new JMenuItem(countType);
                item1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (countType.equals("Используя регулярные выражения")) {
                            // MathEvaluator.evaluateExpressions(expressions);
                            expressions = MathEvaluator.evaluateExpressions(expressions);
                            updateExpressionList();
                        } else if (countType.equals("Без использования регулярных выражений")) {
                            expressions = MathEvaluatorNot.evaluateExpressions(expressions);
                            updateExpressionList();
                        } else if (countType.equals("Используя библиотеку")) {
                            expressions = MathEvaluatorLibrary.evaluateExpressions(expressions);
                            for(int i=0; i<results.size(); i++) {
                                System.out.println(expressions.get(i));
                            }
                            updateExpressionList();
                        }
                    }
                });
                countingMenu.add(item1); // Добавляем элемент в меню
            }

            countingButton.addActionListener(e -> countingMenu.show(countingButton, 0, countingButton.getHeight()));
            panel.add(countingButton);


            JPanel outputFilePanel = new JPanel();
            outputFilePanel.setLayout(new BoxLayout(outputFilePanel, BoxLayout.Y_AXIS));
            outputFilePanel.setBorder(BorderFactory.createTitledBorder("Тип выходного файла"));

            JRadioButton textFileButton = new JRadioButton("Текстовый файл");
            JRadioButton xmlFileButton = new JRadioButton("XML файл");
            JRadioButton jsonFileButton = new JRadioButton("JSON файл");
            JRadioButton yamlFileButton = new JRadioButton("YAML файл");
            textFileButton.setBackground(hexToColor("#E7CFCD"));
            xmlFileButton.setBackground(hexToColor("#E7CFCD"));
            jsonFileButton.setBackground(hexToColor("#E7CFCD"));
            yamlFileButton.setBackground(hexToColor("#E7CFCD"));


            // Группа радио-кнопок
            ButtonGroup outputFileGroup = new ButtonGroup();
            outputFileGroup.add(textFileButton);
            outputFileGroup.add(xmlFileButton);
            outputFileGroup.add(jsonFileButton);
            outputFileGroup.add(yamlFileButton);

            // Добавляем радио-кнопки в панель
            outputFilePanel.add(textFileButton);
            outputFilePanel.add(xmlFileButton);
            outputFilePanel.add(jsonFileButton);
            outputFilePanel.add(yamlFileButton);

            // Добавляем панель с радио-кнопками в основную панель

            textFileButton.setPreferredSize(buttonSize);
            textFileButton.setFont(buttonFont);
            xmlFileButton.setPreferredSize(buttonSize);
            xmlFileButton.setFont(buttonFont);
            jsonFileButton.setPreferredSize(buttonSize);
            jsonFileButton.setFont(buttonFont);
            yamlFileButton.setPreferredSize(buttonSize);
            yamlFileButton.setFont(buttonFont);

            normalFileButton.setBackground(hexToColor("#E7CFCD"));
            normalFileButton.setForeground(Color.BLACK); // Цвет текста
            archivedFileButton.setBackground(hexToColor("#E7CFCD"));
            archivedFileButton.setForeground(Color.BLACK);
            encryptedFileButton.setBackground(hexToColor("#E7CFCD"));
            encryptedFileButton.setForeground(Color.BLACK);
            countingButton.setBackground(hexToColor("#FF8B8B"));
            countingButton.setForeground(Color.BLACK);

            // Добавление кнопок в панель
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(normalFileButton);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(archivedFileButton);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(encryptedFileButton);
            panel.add(Box.createRigidArea(new Dimension(0, 20))); // Разделитель между кнопками
            panel.add(countingButton);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
            outputFilePanel.setBackground(hexToColor("#E7CFCD"));
            panel.add(outputFilePanel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            // Чекбокс для шифрования
            JCheckBox encryptionCheckBox = new JCheckBox("Зашифровать");
            encryptionCheckBox.setBackground(hexToColor("#E7CFCD"));
            encryptionCheckBox.setForeground(Color.BLACK);
            encryptionCheckBox.setPreferredSize(buttonSize);
            encryptionCheckBox.setFont(buttonFont);
            panel.add(encryptionCheckBox);


            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            // Чекбокс для архивации
            JCheckBox archivingCheckBox = new JCheckBox("Заархивировать");
            archivingCheckBox.setBackground(hexToColor("#E7CFCD"));
            archivingCheckBox.setForeground(Color.BLACK);
            archivingCheckBox.setPreferredSize(buttonSize);
            archivingCheckBox.setFont(buttonFont);
            panel.add(encryptionCheckBox);

            panel.add(archivingCheckBox);


            JButton applyButton = new JButton("Применить");


// Добавление обработчика событий для кнопки "Применить"
            applyButton.addActionListener(e -> {
                // Определение типа выходного файла
                String selectedFileType = "";
                if (textFileButton.isSelected()) {
                    selectedFileType = "Текстовый файл";
                } else if (xmlFileButton.isSelected()) {
                    selectedFileType = "XML файл";
                } else if (jsonFileButton.isSelected()) {
                    selectedFileType = "JSON файл";
                } else if (yamlFileButton.isSelected()) {
                    selectedFileType = "YAML файл";
                }

                // Проверка на шифрование
                boolean isEncrypted = encryptionCheckBox.isSelected();
                // Проверка на архивацию
                boolean isArchived = archivingCheckBox.isSelected();

                // Логика применения выбранных параметров
                System.out.println("Тип выходного файла: " + selectedFileType);
                System.out.println("Шифрование: " + (isEncrypted ? "Включено" : "Выключено"));
                System.out.println("Архивация: " + (isArchived ? "Включена" : "Выключена"));

                // Выполнение действий на основе выбранных параметров
                applySettings(selectedFileType, isEncrypted, isArchived, expressions);
            });

// Добавление кнопки "Применить" на основную панель
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            applyButton.setBackground(hexToColor("#FF8B8B"));
            applyButton.setForeground(Color.BLACK);
            panel.add(applyButton);

            // Добавляем панель кнопок в фрейм
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(hexToColor("#CFD8D7")); // Установка фона в розовый цвет
            mainPanel.setLayout(new BorderLayout()); // Установка компоновки

            mainPanel.add(listScrollPane, BorderLayout.WEST); // Список слева
            mainPanel.add(panel, BorderLayout.EAST); // Кнопки справа

            frame.getContentPane().add(mainPanel); // Добавляем основную панель в фрейм

            frame.setVisible(true);



        }

        private void applySettings(String selectedFileType, boolean isEncrypted, boolean isArchived, List<String> expressions ) {
            FileProcessing processor = new FileProcessing();
            FileArchiver archiver = new FileArchiver();
            Path outputPath = null;
            if ( selectedFileType == ("Текстовый файл")) {
                outputPath = Path.of("output.txt");
                try {
                    processor.writeTextFile(outputPath, expressions);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else if ( selectedFileType == ("XML файл")) {
                outputPath = Path.of("output.xml");
                MathExamples mathExamples = new MathExamples();
                List<MathExample> mathExampleList = new ArrayList<>();
                for (String expr : expressions) {
                    MathExample example = new MathExample();
                    example.setExpression(expr);
                    mathExampleList.add(example);
                }
                mathExamples.setExamples(mathExampleList);
                try {
                    processor.writeXmlFile(outputPath, mathExamples);
                } catch (JAXBException ex) {
                    throw new RuntimeException(ex);
                }
            } else if (selectedFileType == ("JSON файл")) {
                outputPath = Path.of("output.json");
                MathExampleList mathExampleList = new MathExampleList();
                List<MathExample> mathExamples = new ArrayList<>();
                for (String expr : expressions) {
                    MathExample example = new MathExample();
                    example.setExpression(expr);
                    mathExamples.add(example);
                }
                mathExampleList.setExamples(mathExamples);
                try {
                    processor.writeJsonFile(outputPath, mathExampleList);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else if (selectedFileType == ("YAML файл")) {
                outputPath = Path.of("output.yaml");
                List<MathExample> mathExamples = new ArrayList<>();
                for (String expr : expressions) {
                    MathExample example = new MathExample();
                    example.setExpression(expr);
                    mathExamples.add(example);
                }
                try {
                    processor.writeYamlFile(outputPath, expressions);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (isEncrypted) {
                try {
                    SecretKey key = FileEncryptor.generateKey(); // Генерация нового ключа
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
            if(isArchived==true){
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


        private Color hexToColor(String hex) {
            return Color.decode(hex); // Преобразование HEX в Color
        }
        private void updateExpressionList() {
            listModel.clear(); // Очищаем текущий список
            for (String expression : expressions) {
                listModel.addElement(expression); // Добавляем новые выражения
            }
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
