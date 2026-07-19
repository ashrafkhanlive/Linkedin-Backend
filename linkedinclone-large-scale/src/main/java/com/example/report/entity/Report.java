package com.example.report.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "reports",
        indexes = {
                @Index(name = "idx_reports_cache_key", columnList = "cacheKey"),
                @Index(name = "idx_reports_status", columnList = "status")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportType reportType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status;

    /** Hash of the request payload, used to dedupe identical requests via cache/DB lookup. */
    @Column(nullable = false, length = 128)
    private String cacheKey;

    /** Free-form JSON string describing the report parameters (date range, filters, etc). */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String parameters;

    @Column(length = 255)
    private String requestedBy;

    @Column(length = 1024)
    private String filePath;

    @Column(length = 1024)
    private String downloadUrl;

    @Column(length = 2000)
    private String errorMessage;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

}
