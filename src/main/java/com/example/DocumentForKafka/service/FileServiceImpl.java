package com.example.DocumentForKafka.service;

import com.example.DocumentForKafka.entity.FileFromService;
import com.example.DocumentForKafka.exception.ProducerProcessException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    /**
     * Путь до папки, в которую поступают документы от бизнеса, для отправки в Kafka
     */
    @Setter
    @Getter
    @Value("${folder.path}")
    private String pathToDocumentFolder;
    /**
     * Корзина для документов, которые не подходят ни под один из топиков
     */
    @Setter
    @Getter
    @Value("${basket.path}")
    private String pathBasket;


    /** Метод считывает данные из папки и добавляет их в список считанных файлов для дальнейшей обработки
     * Файлы с форматом .bad в список не добавляются
     * @return list
     */
    @Override
    public List<String> readFilenames() {
        List<String> list = new ArrayList<>();
        try {
            File folder = new File(pathToDocumentFolder);
            File[] listOfFiles = folder.listFiles();
            if(listOfFiles != null) {
                for(File file : listOfFiles) {
                    if(file.isFile()) {
                        if(!file.getName().endsWith(".bad")) {
                            list.add(file.getName());
                        }
                    }
                }
            } else {
                    log.info("Folder is missing or folder path is incorrectly specified ---> folder.path {}", pathToDocumentFolder);
            }
        } catch (Exception e) {
            throw new ProducerProcessException(e.getMessage());
        }
        return list;
    }
    @Override
    public String readContent(String fileName) {
        try {
            Path filePath = Paths.get(pathToDocumentFolder + fileName);
            return Files.readString(filePath);
        } catch (Exception e) {
            throw new ProducerProcessException("Can't read data ---> readContent()");
        }

    }

    @Override
    public FileFromService transformFileToFileFromService(String name) throws IOException {
        return FileFromService.builder()
                .fileName(name)
                .fileBody(readContent(name))
                .build();
    }

    /**
     * В данный метод должен попасть список только валидных файлов
     * @param fileNames
     * @return
     */
    @Override
    public List<FileFromService> getListFileFromService(List<String> fileNames) {
        var files = checkList(fileNames);
        return files.stream()
                .map(fileName -> {
            try {
                return transformFileToFileFromService(fileName);
            } catch (IOException e) {
                throw new ProducerProcessException(" ---> transformFileToFileFromService() ---> " + fileName);
            }
        }).collect(Collectors.toList());
    }

    /**
     * В метод приходит список файлов из папки 1С
     * Возвращается список валидных файлов для маппинга в список объектов FileFromService
     * Невалидным файлам присваивается расширение .bad для того,
     * чтобы техподдержка могла найти неотправленные файлы по расширению
     * @param files
     * @return listValidFiles
     */
    @Override
    public List<String> checkList(List<String> files) {
        List<String> listValidFiles = new ArrayList<>();
        files.forEach(file -> {
            if(file != null) {
                var subString = changeSubstringToLowerCase(file);
                var newFile = file.replace(file.substring(file.lastIndexOf(".")), subString);
                if (newFile.endsWith(".xml")) {
                    listValidFiles.add(newFile);
                    // для sap
                } else if (newFile.endsWith(".csv")) {
                    listValidFiles.add(newFile);
                    // для sap
                } else if (newFile.endsWith(".json")) {
                    listValidFiles.add(newFile);
                } else {
                    Path filePath = Paths.get(pathToDocumentFolder + newFile);
                    Path filePathTo = Paths.get(pathToDocumentFolder + newFile + ".bad");
                    try {
                        if (!newFile.endsWith(".bad")) {
                            Files.move(filePath, filePathTo, StandardCopyOption.REPLACE_EXISTING);
                            log.info("File {} is not valid - file was renamed", newFile);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        return listValidFiles;
    }

    /**
     * Удаление файла после отправки в Kafka
     * @param file
     */
    @Override
    public void deleteFile(String file) {

        File sendFile = new File(String.valueOf(Paths.get(pathToDocumentFolder, file)));
        if (sendFile.exists()) {
            if (sendFile.delete()) {
                log.info("File {} removed ", file);
            } else {
                log.info("Failed to delete file {} ---> file path {} ", file, pathToDocumentFolder);
            }
        } else {
            throw new IllegalArgumentException("A file  with this name does not exist");
        }

    }

    @Override
    public void createBasketDirectory() {
        if(pathBasket == null || pathBasket.isEmpty()) {
            log.info("Path to the basket folder is empty or incorrectly specified");
        } else {
            Path dirPath = Paths.get(pathBasket + "/basket");
                if (!dirPath.toFile().exists()) {
                    try {
                        Files.createDirectories(dirPath);
                    } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }

    @Override
    public void moveFileToBasket(String fileName) {
        Path dir = Path.of(pathToDocumentFolder + fileName);
        Path dirPath = Path.of(pathBasket + "/basket" + "/" + fileName);
        try {
            Files.move(dir, dirPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File {} sent to folder basket {}", fileName, dirPath);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public String changeSubstringToLowerCase(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

    }


}
