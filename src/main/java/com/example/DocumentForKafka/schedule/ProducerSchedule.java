package com.example.DocumentForKafka.schedule;

import com.example.DocumentForKafka.exception.ProducerProcessException;
import com.example.DocumentForKafka.service.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProducerSchedule {
    private static final Logger log = LoggerFactory.getLogger(ProducerSchedule.class);
    private final ProcessingService processingService;

    @Scheduled(cron = "${cron_job}")
    public void runService() {
        try {
            log.info("Start Producer Process");
            processingService.process();
            log.info("End Producer Process");
        } catch (Exception e) {
            throw new ProducerProcessException(e.getMessage());
        }
    }
}
