package com.example.DocumentForKafka.service;

import com.example.DocumentForKafka.entity.TopicProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessingServiceImpl implements ProcessingService {

    private final FileService fileService;
    private final KafkaService kafkaService;
    private final TopicProperty topicProperty;

    @Value("${topic.name}")
    private String topicName;

    @Override
    public void process() {
            try {
                fileService.createBasketDirectory();
                var filenames = fileService.readFilenames();
                var readyFiles = fileService.getListFileFromService(filenames);
                if (!readyFiles.isEmpty()) {
                    readyFiles.forEach(readyFile -> {
                        var fileName = readyFile.getFileName();
                        String topic = findTopicNameByFilenameType(fileName);
                        try {
                            if(topic != null && !topic.trim().isEmpty()) {
//                                kafkaService.sendDataToKafka(topic, readyFile.getFileName().split("\\.")[1], readyFile.getFileBody());
                                kafkaService.sendDataToKafka(topic, fileName, readyFile.getFileBody());
                                log.info("Send successful to topic : {} : filename : {}", topic, fileName);
                                fileService.deleteFile(fileName);
                            } else {
                                log.info("No topic found for file {}", fileName);
                                log.info("----------------------------------");
                                fileService.moveFileToBasket(fileName);
                            }
                        } catch (IOException e) {
                            log.info(e.getMessage());
                        }
                    });
                } else {
                    log.info("No files to send to Kafka");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    /**
     * Метод находит название топика по имени файла
     * Данные по ключевым словам файлов и топикам находятся в application.yml
     * @param filename
     * @return
     */

    private String findTopicNameByFilenameType(String filename) {
        var collectionItem = topicProperty.getTopicsMapping();
        if(collectionItem.isEmpty()) {
            return topicName;
        }
        for (Map.Entry<String, String> data : collectionItem.entrySet()) {
            if (filename.contains(data.getKey())) {
                return data.getValue();
            }

        }
        return Strings.EMPTY;
    }

}
