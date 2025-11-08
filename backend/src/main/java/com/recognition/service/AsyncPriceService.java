package com.recognition.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncPriceService {

    private final PriceService priceService;

    // Bộ nhớ tạm lưu trạng thái tiến trình (jobId -> status)
    private final Map<String, Map<String, Object>> jobStatus = new ConcurrentHashMap<>();

    public String startJob() {
        String jobId = UUID.randomUUID().toString();
        jobStatus.put(jobId, Map.of("status", "RUNNING", "progress", 0));
        runJobAsync(jobId);
        return jobId;
    }

    @Async
    public void runJobAsync(String jobId) {
        try {
            log.info("Async job {} started", jobId);
            Map<String, Object> result = priceService.fetchAndSaveAllPricesFromFinnhub();
            jobStatus.put(jobId, Map.of(
                    "status", "DONE",
                    "result", result
            ));
            log.info("Async job {} finished", jobId);
        } catch (Exception e) {
            jobStatus.put(jobId, Map.of("status", "FAILED", "error", e.getMessage()));
            log.error("Async job {} failed: {}", jobId, e.getMessage());
        }
    }

    public Map<String, Object> getJobStatus(String jobId) {
        return jobStatus.getOrDefault(jobId, Map.of("status", "NOT_FOUND"));
    }
}

