package com.example.DocumentForKafka.exception;

public class ProducerProcessException extends RuntimeException {
    private static final String MESSAGE = "Process error";
    public ProducerProcessException(String name) {
        super(MESSAGE);
    }
}
