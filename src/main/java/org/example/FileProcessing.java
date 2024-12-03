package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Класс для работы с текстовыми файлами и XML: чтение и запись математических выражений.
 */
public class FileProcessing {

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
}