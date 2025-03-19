package com.example.DocumentForKafka.service;

import java.io.IOException;

public interface ProcessingService {
    void process() throws IOException, InterruptedException;
}
