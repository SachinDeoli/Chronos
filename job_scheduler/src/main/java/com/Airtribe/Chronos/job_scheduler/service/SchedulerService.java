package com.Airtribe.Chronos.job_scheduler.service;

import com.Airtribe.Chronos.constant.AppConstants;
import com.Airtribe.Chronos.enums.JobStatus;
import com.Airtribe.Chronos.entity.Job;
import com.Airtribe.Chronos.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SchedulerService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private KafkaTemplate<String, Job> kafkaTemplate;

    @Scheduled(fixedRate = 5000) // Poll every 5 seconds
    public void pollRedisAndSendToKafka() {
        Set<Object> keys = redisTemplate.opsForHash().keys(AppConstants.JOB_CACHE_KEY);
        Set<Integer> jobIds = keys.stream()
                .map(key -> (Integer) key)
                .collect(Collectors.toSet());
        LocalDateTime now = LocalDateTime.now();

        for (Integer jobId : jobIds) {
            Object cachedJob = redisTemplate.opsForHash().get(AppConstants.JOB_CACHE_KEY, jobId);
            Job job = RedisUtil.convert(cachedJob, Job.class); // Cast to Job entity
            if (job != null && job.getScheduleTime().isBefore(now) && job.getJobStatus().equals(JobStatus.PENDING)) {
                sendToKafka(job);
                redisTemplate.opsForHash().delete(AppConstants.JOB_CACHE_KEY, jobId);
            }
        }
    }

    public void sendToKafka(Job job) {
            kafkaTemplate.send(AppConstants.SCHEDULED_JOBS_TOPIC, job);
    }
}
