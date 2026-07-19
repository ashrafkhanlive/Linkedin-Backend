package com.example.report.dto;

import com.example.report.entity.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Message payload published to Kafka to trigger asynchronous report generation.
 * Kept intentionally small (just the report id + enough context to regenerate)
 * so the topic stays lightweight; the consumer re-reads the full Report row from
 * the DB using reportId as the source of truth.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportGenerationEvent {

    private UUID reportId;
    private ReportType reportType;
    private String requestedBy;
    private Map<String, Object> parameters;

    /** Number of consumer-side retry attempts made so far, for DLT routing decisions. */
    @Builder.Default
    private int retryCount = 0;
}
