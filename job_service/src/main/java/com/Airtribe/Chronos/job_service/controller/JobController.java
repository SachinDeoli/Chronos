package com.Airtribe.Chronos.job_service.controller;

import com.Airtribe.Chronos.job_service.dto.JobDTO;
import com.Airtribe.Chronos.job_service.dto.JobStatus;
import com.Airtribe.Chronos.job_service.entity.Job;
import com.Airtribe.Chronos.job_service.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class JobController {

    @Autowired
    private JobService jobService;

     @GetMapping("/jobs")
     public List<Job> getAllJobs() {
         return jobService.getAllJobs();
     }

     @PostMapping("/jobs")
     public Job createJob(@RequestBody JobDTO job) {
         return jobService.createJob(job);
     }

     @GetMapping("/jobs/{jobId}")
     public Job getJobById(@PathVariable Long jobId) {
         return jobService.getJobById(jobId);
     }

    @GetMapping("/jobs/name/{jobName}")
    public Job getJobByName(@PathVariable String jobName) {
        return jobService.getJobByName(jobName);
    }

     @PutMapping("/jobs/{jobId}")
     public Job updateJob(@PathVariable Long jobId, @RequestBody JobDTO job) {
         return jobService.updateJob(jobId, job);
     }

     @DeleteMapping("/jobs/{jobId}")
     public void deleteJob(@PathVariable Long jobId) {
         jobService.deleteJob(jobId);
     }

     @GetMapping("/jobs/status/{jobStatus}")
     public List<Job> getJobsByStatus(@PathVariable JobStatus jobStatus) {
         return jobService.getJobsByStatus(jobStatus);
     }

     @PostMapping("/jobs/{jobId}/retry")
     public Job retryJob(@PathVariable Long jobId) {
         return jobService.retryJob(jobId);
     }
}
