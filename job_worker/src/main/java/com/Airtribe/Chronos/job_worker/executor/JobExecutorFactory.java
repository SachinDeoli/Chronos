package com.Airtribe.Chronos.job_worker.executor;

import com.Airtribe.Chronos.enums.JobType;
import com.Airtribe.Chronos.job_worker.interfaces.JobExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobExecutorFactory {
    @Autowired
    private EmailJobExecutor emailJobExecutor;

    @Autowired
    private ReminderJobExecutor reminderJobExecutor;

    @Autowired
    private NotificationJobExecutor notificationJobExecutor;

    @Autowired
    private ReportJobExecutor reportJobExecutor;

    public JobExecutor getExecutor(JobType jobType) {
        return switch (jobType) {
             case JobType.Email -> emailJobExecutor;
             case JobType.Notification -> notificationJobExecutor;
             case JobType.Reminder -> reminderJobExecutor;
             case JobType.Report -> reportJobExecutor;
        };
    }
}
