package com.Airtribe.Chronos.job_service.repository;

import com.Airtribe.Chronos.job_service.dto.JobStatus;
import com.Airtribe.Chronos.job_service.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findByJobId(Long jobId);
    List<Job> findByJobStatus(JobStatus status);
    Job findByJobName(String jobName);
}
