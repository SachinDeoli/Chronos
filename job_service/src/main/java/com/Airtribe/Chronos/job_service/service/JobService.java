package com.Airtribe.Chronos.job_service.service;

import com.Airtribe.Chronos.job_service.dto.JobDTO;
import com.Airtribe.Chronos.job_service.dto.JobStatus;
import com.Airtribe.Chronos.job_service.entity.Job;
import com.Airtribe.Chronos.job_service.repository.JobRepository;
import com.Airtribe.Chronos.job_service.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private RedisTemplate <String, Object> redisTemplate;

    private static final String JOB_CACHE_KEY = "JOB_CACHE";

    public List<Job> getAllJobs() {
        List<Object> cachedJobs = redisTemplate.opsForHash().values(JOB_CACHE_KEY);
        if (cachedJobs != null && !cachedJobs.isEmpty()) {
            return cachedJobs.stream()
                    .map(obj -> RedisUtil.convert(obj, Job.class))
                    .collect(Collectors.toList());
        }
        return jobRepository.findAll();
    }

    public Job createJob(JobDTO job) {
        Job j = getJobByName(job.getJobName());
        if(j != null) {
            throw new RuntimeException("Job with the same name already exists");
        }
        else
        {
            Job newJob = new Job();
            newJob.setJobName(job.getJobName());
            newJob.setJobDescription(job.getJobDescription());
            newJob.setJobStatus(JobStatus.PENDING);
            newJob.setJobType(job.getJobType());
            newJob.setRecurrenceType(job.getRecurrenceType());
            newJob.setScheduleTime(job.getScheduleTime());
            newJob.setCreatedAt(LocalDateTime.now());
            newJob.setData(job.getData());

            jobRepository.save(newJob);
            redisTemplate.opsForHash().put(JOB_CACHE_KEY, newJob.getJobId(), newJob);
            return newJob;
        }
    }

    public Job getJobById(Long jobId) {
        Object cachedJob =  redisTemplate.opsForHash().get(JOB_CACHE_KEY, jobId);
        if (cachedJob != null) {
            return RedisUtil.convert(cachedJob, Job.class);
        }
        return jobRepository.findByJobId(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public Job updateJob(Long jobId, JobDTO job) {
        Optional<Job> existingJob = jobRepository.findByJobId(jobId);
        if (existingJob.isPresent()) {
            Job updatedJob = existingJob.get();
            updatedJob.setJobName(job.getJobName());
            updatedJob.setJobDescription(job.getJobDescription());
            updatedJob.setJobType(job.getJobType());
            updatedJob.setRecurrenceType(job.getRecurrenceType());
            updatedJob.setScheduleTime(job.getScheduleTime());
            updatedJob.setData(job.getData());
            updatedJob.setJobStatus(JobStatus.PENDING);

            jobRepository.save(updatedJob);
            redisTemplate.opsForHash().delete(JOB_CACHE_KEY, jobId);
            redisTemplate.opsForHash().put(JOB_CACHE_KEY, updatedJob.getJobId(), updatedJob);
            return updatedJob;
        } else {
            throw new RuntimeException("Job not found");
        }
    }

    public void deleteJob(Long jobId) {
        Job existingJob = getJobById(jobId);
        if (existingJob != null) {
            jobRepository.delete(existingJob);
            redisTemplate.opsForHash().delete(JOB_CACHE_KEY, jobId);
        } else {
            throw new RuntimeException("Job not found");
        }
    }

    public List<Job> getJobsByStatus(JobStatus status) {
        List<Object> cachedJobs = redisTemplate.opsForHash().values(JOB_CACHE_KEY);
        if (cachedJobs != null && !cachedJobs.isEmpty()) {
            return cachedJobs.stream()
                    .map(obj -> RedisUtil.convert(obj, Job.class))
                    .filter(job -> job.getJobStatus() == status)
                    .collect(Collectors.toList());
        }
        return jobRepository.findByJobStatus(status);
    }

    public Job retryJob(Long jobId) {
        Job jobToRetry = getJobById(jobId);
        if (jobToRetry != null) {
            if(jobToRetry.getJobStatus() == JobStatus.PENDING)
            {
                throw new RuntimeException("Job is already in PENDING state");
            }
            jobToRetry.setJobStatus(JobStatus.PENDING);

            jobRepository.save(jobToRetry);
            redisTemplate.opsForHash().delete(JOB_CACHE_KEY, jobId);
            redisTemplate.opsForHash().put(JOB_CACHE_KEY, jobToRetry.getJobId(), jobToRetry);
            return jobToRetry;
        } else {
            throw new RuntimeException("Job not found");
        }
    }

    public Job getJobByName(String jobName) {
        List<Object> cachedJobs = redisTemplate.opsForHash().values(JOB_CACHE_KEY);
        if(cachedJobs !=null && !cachedJobs.isEmpty()) {
            return cachedJobs.stream()
                    .map(obj -> RedisUtil.convert(obj, Job.class))
                    .filter(job -> job.getJobName().equals(jobName))
                    .findFirst()
                    .orElse(null);
        }
        return jobRepository.findByJobName(jobName);
    }
}
