package com.example.report.dto;

import com.example.report.entity.ReportStatus;
import com.example.report.entity.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response returned to the client, both on initial submission and on status polling.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private UUID id;
    private ReportType reportType;
    private ReportStatus status;
    private String downloadUrl;
    private String errorMessage;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean servedFromCache;
}
