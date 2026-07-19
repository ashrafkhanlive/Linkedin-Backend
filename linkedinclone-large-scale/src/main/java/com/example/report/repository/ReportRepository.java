package com.example.report.repository;

import com.example.report.entity.Report;
import com.example.report.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    /**
     * Finds the most recent non-failed report for a given cache key, used to dedupe
     * identical in-flight or already-completed requests without regenerating a file.
     */
    Optional<Report> findFirstByCacheKeyAndStatusInOrderByCreatedAtDesc(
            String cacheKey, java.util.List<ReportStatus> statuses);

    @Modifying
    @Query("update Report r set r.status = :status, r.updatedAt = :updatedAt where r.id = :id")
    int updateStatus(@Param("id") UUID id, @Param("status") ReportStatus status, @Param("updatedAt") Instant updatedAt);

    @Modifying
    @Query("update Report r set r.status = :status, r.filePath = :filePath, r.downloadUrl = :downloadUrl, " +
            "r.updatedAt = :updatedAt where r.id = :id")
    int markCompleted(@Param("id") UUID id,
                       @Param("status") ReportStatus status,
                       @Param("filePath") String filePath,
                       @Param("downloadUrl") String downloadUrl,
                       @Param("updatedAt") Instant updatedAt);

    @Modifying
    @Query("update Report r set r.status = :status, r.errorMessage = :errorMessage, r.updatedAt = :updatedAt " +
            "where r.id = :id")
    int markFailed(@Param("id") UUID id,
                    @Param("status") ReportStatus status,
                    @Param("errorMessage") String errorMessage,
                    @Param("updatedAt") Instant updatedAt);
}
