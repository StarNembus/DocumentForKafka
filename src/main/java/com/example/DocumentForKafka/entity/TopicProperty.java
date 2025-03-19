package com.example.DocumentForKafka.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "topics")
public class TopicProperty {
    private Map<String, String> topicsMapping;
    public TopicProperty(Map<String, String> topicsMapping) {
        this.topicsMapping = topicsMapping;
    }

}
