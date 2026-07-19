package com.example.report.kafka;

import com.example.report.dto.ReportGenerationEvent;
import com.example.report.entity.Report;
import com.example.report.entity.ReportStatus;
import com.example.report.repository.ReportRepository;
import com.example.report.service.FileGenerationService;
import com.example.report.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final ReportRepository reportRepository;
    private final FileGenerationService fileGenerationService;
    private final RedisCacheService redisCacheService;

    @KafkaListener(
            topics = "${report.kafka.topic}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onReportGenerationEvent(ReportGenerationEvent event, Acknowledgment ack) {

        log.info("Received report generation event for reportId={}", event.getReportId());

        Optional<Report> reportOpt = reportRepository.findById(event.getReportId());

        if (reportOpt.isEmpty()) {
            log.warn("Report {} no longer exists, skipping and acknowledging.", event.getReportId());
            ack.acknowledge();
            return;
        }

        Report report = reportOpt.get();

        try {
            // Mark report as processing
            reportRepository.updateStatus(
                    report.getId(),
                    ReportStatus.PROCESSING,
                    Instant.now()
            );

            // Clear cached status
            redisCacheService.evictStatus(report.getCacheKey());

            // Generate report file
            FileGenerationService.GeneratedFile generatedFile =
                    fileGenerationService.generate(
                            report.getId(),
                            event.getReportType(),
                            event.getParameters()
                    );

            // Mark report completed
            reportRepository.markCompleted(
                    report.getId(),
                    ReportStatus.COMPLETED,
                    generatedFile.filePath(),
                    generatedFile.downloadUrl(),
                    Instant.now()
            );

            // Evict cache again so clients see updated status
            redisCacheService.evictStatus(report.getCacheKey());

            log.info(
                    "Report {} generated successfully. Download URL: {}",
                    report.getId(),
                    generatedFile.downloadUrl()
            );

            // Acknowledge only after successful processing
            ack.acknowledge();

        } catch (Exception ex) {

            log.error("Failed to generate report {}", report.getId(), ex);

            try {
                reportRepository.markFailed(
                        report.getId(),
                        ReportStatus.FAILED,
                        truncate(ex.getMessage()),
                        Instant.now()
                );

                redisCacheService.evictStatus(report.getCacheKey());

            } catch (Exception dbEx) {
                log.error("Failed to update report status after processing failure.", dbEx);
            }

            // Re-throw as unchecked exception so Spring Kafka retries
            throw new RuntimeException("Report generation failed for report " + report.getId(), ex);
        }
    }

    private String truncate(String message) {
        if (message == null || message.isBlank()) {
            return "Unknown error";
        }

        return message.length() > 2000
                ? message.substring(0, 2000)
                : message;
    }
}