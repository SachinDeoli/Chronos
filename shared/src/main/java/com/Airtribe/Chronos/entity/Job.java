package com.Airtribe.Chronos.entity;

import com.Airtribe.Chronos.enums.JobStatus;
import com.Airtribe.Chronos.enums.JobType;
import com.Airtribe.Chronos.enums.RecurrenceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    private Long jobId;
    private String jobName;
    private String jobDescription;
    private JobStatus jobStatus;
    private JobType jobType;
    private RecurrenceType recurrenceType;
    private List<LocalTime> scheduleTime;
    private Map<String, LocalDateTime> nextRuns;
    private LocalDateTime createdAt;
    private Map<String, Object> data;
}
