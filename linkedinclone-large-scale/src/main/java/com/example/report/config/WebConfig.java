package com.example.report.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * Exposes locally generated report files at /files/** when running with
 * report.storage.mode=local. In S3 mode this handler is not registered — clients
 * use the S3 URL returned directly in ReportResponse.downloadUrl instead.
 */
@Configuration
@ConditionalOnProperty(name = "report.storage.mode", havingValue = "local", matchIfMissing = true)
public class WebConfig implements WebMvcConfigurer {

    @Value("${report.storage.local-base-path:./generated-reports}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Path.of(basePath).toUri().toString();
        registry.addResourceHandler("/files/**").addResourceLocations(location);
    }
}
