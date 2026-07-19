package com.example.report.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Production storage backend: uploads generated files to S3. Active when
 * report.storage.mode=s3. Requires standard AWS credential resolution
 * (env vars, instance profile, ~/.aws/credentials, etc.) — no credentials are
 * hardcoded here.
 */
@Service
@ConditionalOnProperty(name = "report.storage.mode", havingValue = "s3")
@Slf4j
public class S3FileStorageService implements FileStorageService {

    @Value("${report.storage.s3.bucket}")
    private String bucket;

    @Value("${report.storage.s3.region}")
    private String region;

    private S3Client s3Client() {
        return S3Client.builder().region(Region.of(region)).build();
    }

    @Override
    public String store(Path localFilePath, String objectKey) throws IOException {
        try (S3Client client = s3Client()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build();

            client.putObject(request, localFilePath);

            String downloadUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, objectKey);
            log.info("Uploaded file to S3: {}", downloadUrl);
            return downloadUrl;
        }
    }
}
