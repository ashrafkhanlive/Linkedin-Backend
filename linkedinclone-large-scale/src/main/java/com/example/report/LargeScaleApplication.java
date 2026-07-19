package com.example.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LargeScaleApplication {

    public static void main(String[] args) {
        SpringApplication.run(LargeScaleApplication.class, args);
    }

}
