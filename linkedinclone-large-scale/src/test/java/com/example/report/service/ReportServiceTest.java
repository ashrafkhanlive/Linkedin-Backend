package com.example.report.service;

import com.example.report.dto.ReportRequest;
import com.example.report.dto.ReportResponse;
import com.example.report.entity.ReportType;
import com.example.report.kafka.KafkaProducer;
import com.example.report.repository.ReportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private RedisCacheService redisCacheService;

    @Mock
    private KafkaProducer kafkaProducer;

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reportService = new ReportService(reportRepository, redisCacheService, kafkaProducer, new ObjectMapper());
    }

    @Test
    void submitReport_returnsCachedResponse_whenCacheHit() {
        ReportRequest request = ReportRequest.builder()
                .reportType(ReportType.CSV)
                .requestedBy("alice")
                .parameters(Map.of("region", "APAC"))
                .build();

        ReportResponse cachedResponse = ReportResponse.builder().build();
        when(redisCacheService.buildCacheKey(any(), any(), any())).thenReturn("abc123");
        when(redisCacheService.getStatus("abc123")).thenReturn(Optional.of(cachedResponse));

        ReportResponse result = reportService.submitReport(request);

        assertTrue(result.isServedFromCache());
        verify(reportRepository, never()).save(any());
        verify(kafkaProducer, never()).publishReportGenerationEvent(any());
    }

    @Test
    void submitReport_publishesEvent_whenNoCacheOrDbHit() {
        ReportRequest request = ReportRequest.builder()
                .reportType(ReportType.PDF)
                .requestedBy("bob")
                .parameters(Map.of("year", 2026))
                .build();

        when(redisCacheService.buildCacheKey(any(), any(), any())).thenReturn("key-1");
        when(redisCacheService.getStatus("key-1")).thenReturn(Optional.empty());
        when(reportRepository.findFirstByCacheKeyAndStatusInOrderByCreatedAtDesc(any(), any()))
                .thenReturn(Optional.empty());
        when(reportRepository.save(any())).thenAnswer(invocation -> {
            var report = invocation.getArgument(0, com.example.report.entity.Report.class);
            report.setId(java.util.UUID.randomUUID());
            return report;
        });

        ReportResponse result = reportService.submitReport(request);

        assertFalse(result.isServedFromCache());
        assertNotNull(result.getId());
        verify(kafkaProducer, times(1)).publishReportGenerationEvent(any());
        verify(redisCacheService, times(1)).putStatus(eq("key-1"), any());
    }
}
