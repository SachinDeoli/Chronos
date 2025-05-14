package com.Airtribe.Chronos.job_worker.executor;

import com.Airtribe.Chronos.entity.Job;
import com.Airtribe.Chronos.job_worker.interfaces.JobExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReminderJobExecutor implements JobExecutor {
    @Override
    public void executeJob(Job job) {
        Map<String, Object> data = job.getData();
        String userId = (String) data.get("userId");
        String message = (String) data.get("message");

        System.out.println("⏰ Reminder for user " + userId + ": " + message);
    }
}

