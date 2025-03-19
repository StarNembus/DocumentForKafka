package com.example.DocumentForKafka.producer;
import com.example.DocumentForKafka.entity.FileFromService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FileProducer {
    private final KafkaTemplate<String, FileFromService> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(FileProducer.class);
    private final String createFileTopic;


    public FileProducer(KafkaTemplate<String, FileFromService> kafkaTemplate, @Value("Default") String createFileTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.createFileTopic = createFileTopic;
    }


    public boolean sendCreateFileEvent(FileFromService fileFrom) throws ExecutionException, InterruptedException {
        SendResult<String, FileFromService> sendResult = kafkaTemplate.send(createFileTopic, fileFrom).get();
        log.info("Create file {} event send Kafka", fileFrom);
        log.info(sendResult.toString());
        return true;
    }
}
