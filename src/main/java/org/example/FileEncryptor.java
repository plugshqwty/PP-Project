package org.example;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.CipherOutputStream;
import javax.crypto.CipherInputStream;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;

public class FileEncryptor {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    // Генерация ключа
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // Длина ключа
        return keyGen.generateKey();
    }

    // Генерация вектора инициализации
    public static byte[] generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    // Шифрование файла
    public static void encryptFile(Path inputFile, Path outputFile, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        try (CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(outputFile.toFile()), cipher);
             FileInputStream fis = new FileInputStream(inputFile.toFile())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }
    }

    // Расшифровка файла
    public static void decryptFile(Path inputFile, Path outputFile, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        try (CipherInputStream cis = new CipherInputStream(new FileInputStream(inputFile.toFile()), cipher);
             FileOutputStream fos = new FileOutputStream(outputFile.toFile())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    // Сохранение IV в файл
    public static void saveIv(byte[] iv, Path ivFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(ivFile.toFile())) {
            fos.write(iv);
        }
    }

    // Загрузка IV из файла
    public static byte[] loadIv(Path ivFile) throws IOException {
        return Files.readAllBytes(ivFile);
    }

    // Сохранение ключа в файл
    public static void saveKey(SecretKey key, Path keyFile) throws IOException {
        byte[] keyBytes = key.getEncoded();
        try (FileOutputStream fos = new FileOutputStream(keyFile.toFile())) {
            fos.write(keyBytes);
        }
    }

    // Загрузка ключа из файла
    public static SecretKey loadKey(Path keyFile) throws IOException {
        byte[] keyBytes = Files.readAllBytes(keyFile);
        return new SecretKeySpec(keyBytes, "AES");
    }
}