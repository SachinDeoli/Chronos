package com.Airtribe.Chronos.job_worker.kafka;

import com.Airtribe.Chronos.constant.AppConstants;
import com.Airtribe.Chronos.entity.Job;
import com.Airtribe.Chronos.job_worker.executor.JobExecutorFactory;
import com.Airtribe.Chronos.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    @Autowired
    private JobExecutorFactory jobExecutorFactory;

    @KafkaListener(topics = AppConstants.SCHEDULED_JOBS_TOPIC, groupId = AppConstants.JOB_WORKER_GROUP_ID)
    public void consume(Job job) throws Exception {
        jobExecutorFactory.getExecutor(job.getJobType()).executeJob(job);
    }
}
