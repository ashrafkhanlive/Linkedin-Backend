package com.example.report.dto;

import com.example.report.entity.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Incoming request payload for generating a report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {

    @NotNull(message = "reportType is required (CSV, EXCEL, PDF)")
    private ReportType reportType;

    @NotBlank(message = "requestedBy is required")
    private String requestedBy;

    /** Arbitrary filter/parameters used to build the report content, e.g. date range, region. */
    private Map<String, Object> parameters;
}
