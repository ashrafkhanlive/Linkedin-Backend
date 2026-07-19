package com.example.report.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Default storage backend: copies generated files into a local directory and
 * serves them via a "download URL" that a real deployment would front with
 * nginx/S3/a static file controller. Active when report.storage.mode=local (default).
 */
@Service
@ConditionalOnProperty(name = "report.storage.mode", havingValue = "local", matchIfMissing = true)
@Slf4j
public class LocalFileStorageService implements FileStorageService {

    @Value("${report.storage.local-base-path:./generated-reports}")
    private String basePath;

    @Override
    public String store(Path localFilePath, String objectKey) throws IOException {
        Path targetDir = Path.of(basePath);
        Files.createDirectories(targetDir);

        Path target = targetDir.resolve(objectKey);
        Files.createDirectories(target.getParent());
        Files.copy(localFilePath, target, StandardCopyOption.REPLACE_EXISTING);

        String downloadUrl = "/files/" + objectKey.replace("\\", "/");
        log.info("Stored file locally at {} (served at {})", target, downloadUrl);
        return downloadUrl;
    }
}
