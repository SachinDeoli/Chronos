package com.Airtribe.Chronos.job_worker.executor;

import com.Airtribe.Chronos.entity.Job;
import com.Airtribe.Chronos.job_worker.interfaces.JobExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationJobExecutor implements JobExecutor {
    @Override
    public void executeJob(Job job) {
        // Implement the logic to execute the notification job
        System.out.println("Executing NOTIFICATION job: " + job);
        Map<String, Object> data = job.getData();
        String message = (String) data.getOrDefault("message", "No message provided");
        System.out.println("🔔 Notification: " + message);
    }
}
