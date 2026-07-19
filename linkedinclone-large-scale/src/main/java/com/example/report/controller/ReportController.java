package com.example.report.controller;

import com.example.report.dto.ReportRequest;
import com.example.report.dto.ReportResponse;
import com.example.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Submit and track asynchronous report generation")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "Submit a new report generation request")
    public ResponseEntity<ReportResponse> submitReport(@Valid @RequestBody ReportRequest request) {
        ReportResponse response = reportService.submitReport(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get the status/result of a previously submitted report")
    public ResponseEntity<ReportResponse> getReportStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(reportService.getReportStatus(id));
    }
}
