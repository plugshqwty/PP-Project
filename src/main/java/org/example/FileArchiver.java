package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileArchiver {

    // Метод для архивации файла
    public void archiveFile(Path sourceFilePath, Path archiveFilePath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(archiveFilePath.toFile()))) {
            ZipEntry zipEntry = new ZipEntry(sourceFilePath.getFileName().toString());
            zos.putNextEntry(zipEntry);

            Files.copy(sourceFilePath, zos);
            zos.closeEntry();
        }
    }

    // Метод для разархивации файла
    public void extractFile(Path archiveFilePath, Path outputDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archiveFilePath.toFile()))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                Path newFilePath = outputDir.resolve(zipEntry.getName());

                // Создаем необходимые директории
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newFilePath);
                } else {
                    // Записываем файл
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFilePath.toFile()))) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            bos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
}