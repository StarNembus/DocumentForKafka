package com.example.DocumentForKafka.service;

import java.io.IOException;

public interface KafkaService {
    void sendDataToKafka(String topic, String key, String message) throws IOException;

}
