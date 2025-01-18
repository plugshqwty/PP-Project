package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.util.ArrayList;
import java.util.Map;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.yaml.snakeyaml.Yaml;





/*
 * Класс для работы с текстовыми файлами и XML: чтение и запись математических выражений.
 */
public class FileProcessing {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Yaml yaml = new Yaml();


    // Чтение из текстового файла
    public List<String> readTextFile(Path path) throws IOException {
        return Files.readAllLines(path);
    }

    // Запись в текстовый файл
    public void writeTextFile(Path path, List<String> lines) throws IOException {
        Files.write(path, lines);
    }

    // Чтение из XML файла
    public MathExamples readXmlFile(Path path) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(MathExamples.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (MathExamples) unmarshaller.unmarshal(path.toFile());
    }

    // Запись в XML файл
    public void writeXmlFile(Path path, MathExamples mathExamples) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(MathExamples.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(mathExamples, path.toFile());
    }

    // Чтение из JSON файла
    public MathExampleList readJsonFile(Path path) throws IOException {
        return gson.fromJson(Files.newBufferedReader(path), MathExampleList.class);
    }

    // Запись в JSON файл
    public void writeJsonFile(Path path, MathExampleList mathExampleList) throws IOException {
        try (var writer = Files.newBufferedWriter(path)) {
            gson.toJson(mathExampleList, writer);
        }
    }

    // Чтение из YAML файла
    public List<MathExample> readYamlFile(Path path) throws IOException {
        List<MathExample> examples = new ArrayList<>();

        try (var reader = Files.newBufferedReader(path)) {
            Iterable<Object> iterable = yaml.loadAll(reader);

            // Проход по каждому объекту в загруженных данных
            for (Object obj : iterable) {
                if (obj instanceof List) { // Проверяем, является ли объект списком
                    List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
                    for (Map<String, Object> map : list) {
                        String expression = (String) map.get("expression");
                        MathExample example = new MathExample();
                        example.setExpression(expression);
                        examples.add(example);
                    }
                } else if (obj instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) obj;
                    String expression = (String) map.get("expression");
                    MathExample example = new MathExample();
                    example.setExpression(expression);
                    examples.add(example);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            throw e; // Пробрасываем исключение дальше
        }

        return examples;
    }

    // Запись в YAML файл
    public void writeYamlFile(Path path, List<String> expressions) throws IOException {
        try (var writer = Files.newBufferedWriter(path)) {
            Yaml yaml = new Yaml(); // Создание экземпляра Yaml
            List<Map<String, String>> data = new ArrayList<>();

            // Заполнение списка карт с выражениями
            for (String expression : expressions) {
                Map<String, String> entry = new HashMap<>();
                entry.put("expression", expression);
                data.add(entry);
            }

            // Запись данных в файл
            yaml.dump(data, writer);
        }
    }



    public static class Builder {
        private Gson gson = new GsonBuilder().setPrettyPrinting().create();
        private Yaml yaml = new Yaml();

        public Builder setGson(Gson gson) {
            this.gson = gson;
            return this;
        }

        public Builder setYaml(Yaml yaml) {
            this.yaml = yaml;
            return this;
        }

        public FileProcessing build() {
            FileProcessing fileProcessing = new FileProcessing();
            fileProcessing.gson = this.gson;
            fileProcessing.yaml = this.yaml;
            return fileProcessing;
        }
    }
}



