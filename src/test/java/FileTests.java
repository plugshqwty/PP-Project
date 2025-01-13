package org.example;

import org.junit.jupiter.api.*;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileProcessingTest {
    private FileProcessing fileProcessing;
    private Path tempFile;
    private Path tempXmlFile;

    @BeforeEach
    void setUp() throws IOException {
        fileProcessing = new FileProcessing();
        tempFile = Files.createTempFile("testFile", ".txt");
        tempXmlFile = Files.createTempFile("testFile", ".xml");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
        Files.deleteIfExists(tempXmlFile);
    }

    @Test
    void testReadTextFile() throws IOException {
        Files.write(tempFile, List.of("line1", "line2", "line3"));
        List<String> lines = fileProcessing.readTextFile(tempFile);
        assertEquals(List.of("line1", "line2", "line3"), lines);
    }

    @Test
    void testWriteTextFile() throws IOException {
        fileProcessing.writeTextFile(tempFile, List.of("line1", "line2"));
        List<String> lines = Files.readAllLines(tempFile);
        assertEquals(List.of("line1", "line2"), lines);
    }

    @Test
    void testReadXmlFile() throws Exception {
        MathExamples mathExamples = new MathExamples();
        MathExample example1 = new MathExample();
        example1.setExpression("x + y");
        MathExample example2 = new MathExample();
        example2.setExpression("a - b");
        mathExamples.setExamples(List.of(example1, example2));

        fileProcessing.writeXmlFile(tempXmlFile, mathExamples);

        MathExamples readExamples = fileProcessing.readXmlFile(tempXmlFile);
        assertEquals(2, readExamples.getExamples().size());
        assertEquals("x + y", readExamples.getExamples().get(0).getExpression());
    }

    @Test
    void testWriteXmlFile() throws Exception {
        MathExamples mathExamples = new MathExamples();
        MathExample example = new MathExample();
        example.setExpression("x + y");
        mathExamples.setExamples(List.of(example));

        fileProcessing.writeXmlFile(tempXmlFile, mathExamples);

        MathExamples readExamples = fileProcessing.readXmlFile(tempXmlFile);
        assertEquals("x + y", readExamples.getExamples().get(0).getExpression());
    }
}

// Тесты для работы с YAML файлами
class FileProcessingYamlTest {
    private FileProcessing fileProcessing;
    private Path tempYamlFile;

    @BeforeEach
    void setUp() throws IOException {
        fileProcessing = new FileProcessing();
        tempYamlFile = Files.createTempFile("testFile", ".yaml");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempYamlFile);
    }

    @Test
    void testReadYamlFile() throws IOException {
        String yamlContent = "- expression: x + y\n- expression: a - b\n";
        Files.write(tempYamlFile, yamlContent.getBytes());

        List<MathExample> readExamples = fileProcessing.readYamlFile(tempYamlFile);
        assertEquals(2, readExamples.size());
        assertEquals("x + y", readExamples.get(0).getExpression());
    }

    @Test
    void testWriteYamlFile() throws IOException {
        List<String> expressions = List.of("x + y", "a - b");
        fileProcessing.writeYamlFile(tempYamlFile, expressions);

        List<MathExample> readExamples = fileProcessing.readYamlFile(tempYamlFile);
        assertEquals(2, readExamples.size());
        assertEquals("x + y", readExamples.get(0).getExpression());
    }
}

// Тесты для работы с JSON файлами
class FileProcessingJsonTest {
    private FileProcessing fileProcessing;
    private Path tempJsonFile;

    @BeforeEach
    void setUp() throws IOException {
        fileProcessing = new FileProcessing();
        tempJsonFile = Files.createTempFile("testFile", ".json");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempJsonFile);
    }

    @Test
    void testReadJsonFile() throws IOException {
        MathExampleList mathExampleList = new MathExampleList();
        MathExample example1 = new MathExample();
        example1.setExpression("x + y");
        MathExample example2 = new MathExample();
        example2.setExpression("a - b");
        mathExampleList.setExamples(List.of(example1, example2));

        fileProcessing.writeJsonFile(tempJsonFile, mathExampleList);

        MathExampleList readExamples = fileProcessing.readJsonFile(tempJsonFile);
        assertEquals(2, readExamples.getExamples().size());
        assertEquals("x + y", readExamples.getExamples().get(0).getExpression());
    }

    @Test
    void testWriteJsonFile() throws IOException {
        MathExampleList mathExampleList = new MathExampleList();
        MathExample example = new MathExample();
        example.setExpression("x + y");
        mathExampleList.setExamples(List.of(example));

        fileProcessing.writeJsonFile(tempJsonFile, mathExampleList);

        MathExampleList readExamples = fileProcessing.readJsonFile(tempJsonFile);
        assertEquals("x + y", readExamples.getExamples().get(0).getExpression());
    }
}

class FileArchiverTest {
    private FileArchiver fileArchiver;
    private Path tempFile;
    private Path archivePath;

    @BeforeEach
    void setUp() throws IOException {
        fileArchiver = new FileArchiver();
        tempFile = Files.createTempFile("testFile", ".txt");
        archivePath = Files.createTempFile("testArchive", ".zip");
        Files.write(tempFile, List.of("test content"));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
        Files.deleteIfExists(archivePath);
    }

    @Test
    void testArchiveFile() throws IOException {
        fileArchiver.archiveFile(tempFile, archivePath);
        assertTrue(Files.exists(archivePath));
    }

    @Test
    void testExtractFile() throws IOException {
        fileArchiver.archiveFile(tempFile, archivePath);
        Path outputDir = Files.createTempDirectory("extracted");
        fileArchiver.extractFile(archivePath, outputDir);

        Path extractedFile = outputDir.resolve(tempFile.getFileName());
        assertTrue(Files.exists(extractedFile));

        List<String> lines = Files.readAllLines(extractedFile);
        assertEquals(List.of("test content"), lines);
    }
}

class FileEncryptorTest {
    private FileEncryptor fileEncryptor;
    private Path tempFile;
    private Path encryptedFile;
    private SecretKey key;
    private byte[] iv;

    @BeforeEach
    void setUp() throws Exception {
        fileEncryptor = new FileEncryptor();
        tempFile = Files.createTempFile("testFile", ".txt");
        encryptedFile = Files.createTempFile("testFile", ".enc");
        Files.write(tempFile, List.of("test content"));
        key = fileEncryptor.generateKey();
        iv = fileEncryptor.generateIv();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
        Files.deleteIfExists(encryptedFile);
    }

    @Test
    void testEncryptFile() throws Exception {
        fileEncryptor.encryptFile(tempFile, encryptedFile, key, iv);
        assertTrue(Files.exists(encryptedFile));
    }

    @Test
    void testDecryptFile() throws Exception {
        fileEncryptor.encryptFile(tempFile, encryptedFile, key, iv);
        Path decryptedFile = Files.createTempFile("decrypted", ".txt");
        fileEncryptor.decryptFile(encryptedFile, decryptedFile, key, iv);

        List<String> lines = Files.readAllLines(decryptedFile);
        assertEquals(List.of("test content"), lines);
    }
}