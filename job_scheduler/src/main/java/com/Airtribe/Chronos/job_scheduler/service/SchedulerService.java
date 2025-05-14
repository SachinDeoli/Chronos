package com.Airtribe.Chronos.job_scheduler.service;

import com.Airtribe.Chronos.constant.AppConstants;
import com.Airtribe.Chronos.enums.JobStatus;
import com.Airtribe.Chronos.entity.Job;
import com.Airtribe.Chronos.enums.RecurrenceType;
import com.Airtribe.Chronos.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Map;
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

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        for (Integer jobId : jobIds) {
            Object cachedJob = redisTemplate.opsForHash().get(AppConstants.JOB_CACHE_KEY, jobId);
            Job job = RedisUtil.convert(cachedJob, Job.class);

            if (job == null || job.getJobStatus() != JobStatus.PENDING) continue;
            Map<String, LocalDateTime> nextRuns = job.getNextRuns();

            if(nextRuns == null || nextRuns.isEmpty())
            {
                redisTemplate.opsForHash().delete(AppConstants.JOB_CACHE_KEY, jobId);
                continue;
            }

            Iterator<Map.Entry<String, LocalDateTime>> iterator = nextRuns.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, LocalDateTime> entry = iterator.next();
                LocalDateTime scheduledTime = entry.getValue().truncatedTo(ChronoUnit.MINUTES);

                if (!scheduledTime.isAfter(now)) {
                    sendToKafka(job);

                    if (job.getRecurrenceType() == null) {
                        // One-time job: remove this time slot after execution
                        iterator.remove();
                    } else {
                        // Recurring job: update next scheduled run time
                        LocalDateTime nextRun = calculateNextRun(scheduledTime, job.getRecurrenceType());
                        entry.setValue(nextRun);
                    }
                    job.setNextRuns(nextRuns);
                    redisTemplate.opsForHash().put(AppConstants.JOB_CACHE_KEY, jobId, job);
                }
            }
        }
    }

    private LocalDateTime calculateNextRun(LocalDateTime current, RecurrenceType type) {
        return switch (type) {
            case Daily -> current.plusDays(1);
            case Weekly -> current.plusWeeks(1);
            case Monthly -> current.plusMonths(1);
        };
    }

    public void sendToKafka(Job job) {
            kafkaTemplate.send(AppConstants.SCHEDULED_JOBS_TOPIC, job);
    }
}
