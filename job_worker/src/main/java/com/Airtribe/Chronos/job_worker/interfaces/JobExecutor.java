package com.Airtribe.Chronos.job_worker.interfaces;

import com.Airtribe.Chronos.entity.Job;

import java.util.Map;

public interface JobExecutor {
    void executeJob(Job job);
}
