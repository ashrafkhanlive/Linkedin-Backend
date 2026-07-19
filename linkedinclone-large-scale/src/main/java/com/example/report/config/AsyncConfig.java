package com.example.report.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Configures Java 21 virtual-thread-per-task executors for @Async methods
 * (e.g. FileGenerationService) so blocking I/O like file writes and S3 uploads
 * don't tie up limited platform threads.
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "virtualThreadExecutor")
    public Executor getAsyncExecutor() {
        AsyncTaskExecutor executor = new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) ->
                System.err.printf("Async error in method '%s': %s%n", method.getName(), throwable.getMessage());
    }
}
