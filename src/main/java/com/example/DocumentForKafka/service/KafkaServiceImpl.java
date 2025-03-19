package com.example.DocumentForKafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendDataToKafka(String topic, String key, String fileMessage) {
            kafkaTemplate.send(topic, key, fileMessage);
            log.info("Sending : topic {} ", topic);

    }
}
