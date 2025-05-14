package com.Airtribe.Chronos.job_worker.service;

import org.springframework.stereotype.Service;

@Service
public class WorkerService {
    //This class will be called when the EmailJobExecutor, ReminderJobExecutor, NotificationJobExecutor, and ReportJobExecutor are called annd then this class will update the Database
    public void updateDatabase(String jobId, String status) {
        // Logic to update the database with the job status
        // For example, you can use a repository to update the job status in the database
        System.out.println("Updating job with ID: " + jobId + " to status: " + status);
    }

}
