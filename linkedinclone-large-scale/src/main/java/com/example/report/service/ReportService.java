package com.example.report.service;

import com.example.report.dto.ReportGenerationEvent;
import com.example.report.dto.ReportRequest;
import com.example.report.dto.ReportResponse;
import com.example.report.entity.Report;
import com.example.report.entity.ReportStatus;
import com.example.report.exception.ReportNotFoundException;
import com.example.report.kafka.KafkaProducer;
import com.example.report.repository.ReportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final RedisCacheService redisCacheService;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    private static final List<ReportStatus> DEDUPE_STATUSES =
            List.of(ReportStatus.PENDING, ReportStatus.PROCESSING, ReportStatus.COMPLETED);

    /**
     * Submits a new report request.
     * Flow: check Redis cache -> check DB for an identical in-flight/completed request
     * -> otherwise persist a new PENDING report and publish a Kafka event to trigger
     * asynchronous generation.
     */
    @Transactional
    public ReportResponse submitReport(ReportRequest request) {
        String parametersJson = toJson(request.getParameters());
        String cacheKey = redisCacheService.buildCacheKey(
                request.getReportType().name(), request.getRequestedBy(), parametersJson);

        // 1. Redis cache check
        Optional<ReportResponse> cached = redisCacheService.getStatus(cacheKey);
        if (cached.isPresent()) {
            log.info("Cache hit for cacheKey={}", cacheKey);
            ReportResponse response = cached.get();
            response.setServedFromCache(true);
            return response;
        }

        // 2. DB dedupe check (covers cache misses after eviction/restart)
        Optional<Report> existing = reportRepository.findFirstByCacheKeyAndStatusInOrderByCreatedAtDesc(
                cacheKey, DEDUPE_STATUSES);
        if (existing.isPresent()) {
            ReportResponse response = toResponse(existing.get(), false);
            redisCacheService.putStatus(cacheKey, response);
            return response;
        }

        // 3. Persist new request
        Report report = Report.builder()
                .reportType(request.getReportType())
                .status(ReportStatus.PENDING)
                .cacheKey(cacheKey)
                .parameters(parametersJson)
                .requestedBy(request.getRequestedBy())
                .build();
        report = reportRepository.save(report);

        // 4. Publish Kafka event to trigger async generation
        ReportGenerationEvent event = ReportGenerationEvent.builder()
                .reportId(report.getId())
                .reportType(report.getReportType())
                .requestedBy(report.getRequestedBy())
                .parameters(request.getParameters())
                .build();
        kafkaProducer.publishReportGenerationEvent(event);

        // 5. Cache the pending status so rapid duplicate calls short-circuit
        ReportResponse response = toResponse(report, false);
        redisCacheService.putStatus(cacheKey, response);

        return response;
    }

    @Transactional(readOnly = true)
    public ReportResponse getReportStatus(UUID id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException("Report not found: " + id));
        return toResponse(report, false);
    }

    private ReportResponse toResponse(Report report, boolean fromCache) {
        return ReportResponse.builder()
                .id(report.getId())
                .reportType(report.getReportType())
                .status(report.getStatus())
                .downloadUrl(report.getDownloadUrl())
                .errorMessage(report.getErrorMessage())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .servedFromCache(fromCache)
                .build();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj == null ? Collections.emptyMap() : obj);
        } catch (Exception e) {
            log.warn("Failed to serialize report parameters, falling back to toString()", e);
            return String.valueOf(obj);
        }
    }
}
