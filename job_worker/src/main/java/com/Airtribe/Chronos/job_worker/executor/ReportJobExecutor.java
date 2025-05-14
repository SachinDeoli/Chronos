package com.Airtribe.Chronos.job_worker.executor;

import com.Airtribe.Chronos.entity.Job;
import com.Airtribe.Chronos.job_worker.interfaces.JobExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReportJobExecutor implements JobExecutor {
    @Override
    public void executeJob(Job job) {
        Map<String, Object> data = job.getData();
        String reportName = (String) data.getOrDefault("reportName", "DefaultReport");
        String reportType = (String) data.getOrDefault("reportType", "PDF");

        System.out.println("📄 Generating report: " + reportName + " (" + reportType + ")");

        // Simulate generation time
        try {
            Thread.sleep(2000); // Simulating report creation delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("✅ Report generated successfully: " + reportName);
    }
}

