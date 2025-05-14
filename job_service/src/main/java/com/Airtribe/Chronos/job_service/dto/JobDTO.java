package com.Airtribe.Chronos.job_service.dto;
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
public class JobDTO {
    private String jobName;
    private String jobDescription;
    private JobType jobType;
    private RecurrenceType recurrenceType;
    private List<LocalTime> scheduleTime;
    private Map<String, Object> data;
}
