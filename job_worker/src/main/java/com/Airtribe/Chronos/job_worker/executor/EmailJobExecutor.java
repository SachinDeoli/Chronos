package com.Airtribe.Chronos.job_worker.executor;

import com.Airtribe.Chronos.entity.Job;
import com.Airtribe.Chronos.job_worker.interfaces.JobExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmailJobExecutor implements JobExecutor {
    @Override
    public void executeJob(Job job) {
        Map<String, Object> data = job.getData();

        String recipient = (String) data.get("email");
        String subject = (String) data.get("subject");
        String message = (String) data.get("message");

        // Simulate sending an email
        System.out.println("=== Sending Email ===");
        System.out.println("To: " + recipient);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("=====================");
    }
}
