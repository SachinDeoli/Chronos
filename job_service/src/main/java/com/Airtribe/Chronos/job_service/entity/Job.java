package com.Airtribe.Chronos.job_service.entity;

import com.Airtribe.Chronos.job_service.converter.LocalDateTimeMapConverter;
import com.Airtribe.Chronos.job_service.converter.ObjectMapConverter;
import com.Airtribe.Chronos.job_service.dto.JobStatus;
import com.Airtribe.Chronos.job_service.dto.JobType;
import com.Airtribe.Chronos.job_service.dto.RecurrenceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long jobId;

    @NotNull
    private String jobName;

    private String jobDescription;

    @NotNull
    @Enumerated(EnumType.STRING)
    private JobStatus jobStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    private RecurrenceType recurrenceType;

    @NotNull
    private List<LocalTime> scheduleTime;

    @NotNull
    @Column(columnDefinition = "jsonb")
    @Convert(converter = LocalDateTimeMapConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private Map<String, LocalDateTime> nextRuns;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    @Column(columnDefinition = "jsonb")
    @Convert(converter = ObjectMapConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private Map<String, Object> data;

}
