package com.example.report.kafka;

import com.example.report.dto.ReportGenerationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, ReportGenerationEvent> kafkaTemplate;

    @Value("${report.kafka.topic}")
    private String reportTopic;

    public void publishReportGenerationEvent(ReportGenerationEvent event) {
        // Key by reportId so all events for the same report land on the same partition,
        // preserving ordering for that report (important if retries re-publish).
        String key = event.getReportId().toString();

        kafkaTemplate.send(reportTopic, key, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish report generation event for reportId={}", event.getReportId(), ex);
            } else {
                log.info("Published report generation event reportId={} to partition={} offset={}",
                        event.getReportId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
