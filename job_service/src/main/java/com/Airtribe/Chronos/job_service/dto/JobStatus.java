package com.Airtribe.Chronos.job_service.dto;

public enum JobStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED,
    SCHEDULED,
    PAUSED,
    RESUMED,
    RETRYING,
    TIMEOUT,
    SKIPPED
}
