# Chronos - Distributed Job Scheduler

Chronos is a distributed job scheduling system designed to handle one-time and recurring tasks with configurable schedules (daily, weekly, monthly). It is built using a microservices architecture with Spring Boot, Redis, Kafka, and PostgreSQL.

## Table of Contents

* [Architecture Overview](#architecture-overview)
* [Modules](#modules)
* [Job Scheduling Use Cases](#job-scheduling-use-cases)
* [How Scheduling Works](#how-scheduling-works)
* [Setup & Installation](#setup--installation)
* [API Behavior](#api-behavior)
* [Design Decisions](#design-decisions)
* [Future Enhancements](#future-enhancements)

---

## Architecture Overview

Chronos consists of four modules:

1. **job\_service**: Handles job creation, validation, and persistence.
2. **job\_scheduler**: Periodically polls Redis for pending jobs and sends them to Kafka.
3. **job\_worker**: Consumes jobs from Kafka and performs execution.
4. **shared**: Contains common entities, enums, and utility classes shared across modules.

All microservices interact through:

* Redis (as an in-memory job queue and cache)
* Kafka (as a messaging queue)
* PostgreSQL (for persistent storage)

---

## Modules

### 1. job\_service

* Receives job submissions via REST API.
* Stores job in PostgreSQL.
* Generates `nextRuns` (Map\<String, LocalDateTime>) based on recurrence type and schedule times.
* Caches job in Redis with key: `JOB_CACHE_KEY`.

### 2. job\_scheduler

* Polls Redis every 5 seconds via `@Scheduled` method.
* Compares `nextRuns` against `now.truncatedTo(MINUTES)`.
* Sends due jobs to Kafka.
* Removes fired times from `nextRuns`.
* If `nextRuns` is empty:

  * Marks job as COMPLETED in DB.
  * Deletes job from Redis.

### 3. job\_worker

* Consumes jobs from Kafka.
* Executes the job logic (e.g., shell scripts, APIs).
* Logs execution and errors (can be extended).

### 4. shared

* Houses all shared logic, models, and constants.
* No business logic, only common code.

---

## Job Scheduling Use Cases

1. **One-Time Jobs**

   * Run at specific times on a specific day.
   * E.g., May 14 at 10 AM and 5 PM.

2. **Daily Recurrence**

   * Run every day at 10 AM / 5 PM or both.

3. **Weekly Recurrence**

   * Run every Monday at 10 AM / 5 PM or both.

4. **Monthly Recurrence**

   * Run every 14th of the month at 10 AM / 5 PM or both.

`Job.scheduleTime = List<LocalTime>` holds multiple times per day.

`Job.recurrenceType = null` indicates one-time job.

---

## How Scheduling Works

### In job\_service:

1. When a job is created:

   * It calculates all `nextRuns` for each time (10 AM, 5 PM, etc.).
   * Saves job in PostgreSQL.
   * Pushes job to Redis with key `JOB_CACHE_KEY`.

### In job\_scheduler:

2. Scheduler runs every 5 seconds:

   * Fetches all jobs from Redis.
   * For each job:

     * Skips if status != PENDING.
     * Compares `now` (truncated to minutes) with each entry in `nextRuns`.
     * If `runTime <= now`, sends job to Kafka.
     * Removes the executed time from `nextRuns`.
     * If job has recurrence, calculates next run.
     * If `nextRuns` is empty:

       * Marks job as COMPLETED in DB.
       * Deletes from Redis.

### In job\_worker:

3. Consumes Kafka job and executes logic.

---

## Setup & Installation

### Requirements:

* Java 17+
* Maven
* Redis
* Kafka
* PostgreSQL

### Setup Steps:

```bash
# Clone repo
$ git clone <repo_url>
$ cd chronos

# Start Redis, Kafka, and Postgres (can use Docker Compose)

# Build and install all modules
$ mvn clean install

# Start services individually:
$ cd job_service && mvn spring-boot:run
$ cd job_scheduler && mvn spring-boot:run
$ cd job_worker && mvn spring-boot:run
```

### Redis Configuration:

* All jobs are stored under `JOB_CACHE_KEY` hash.

### Kafka Topics:

* `job-execution-topic`

---

## API Behavior

### POST /jobs

* Accepts JobDTO:

```json
{
  "jobName": "SendWelcomeEmail19",
  "jobDescription": "Sends a welcome email to the user",
  "jobType": "Email",
  "recurrenceType": "DAILY",   OR //Daily, Weekly, Monthly or null for one time
  "scheduleTime": ["23:34", "23:35"],
  "data": {
    "email": "user@example.com",
    "subject": "Welcome!",
    "message": "Thanks for signing up!"
  }
}
```

* Calculates next runs and saves job.

---

## Design Decisions

* **Redis-first execution**: Redis holds real-time triggers, keeping DB access minimal.
* **Modular microservices**: Easy to scale horizontally and decouple responsibilities.
* **Kafka for decoupling**: Enables retry and async processing for job execution.
* **No DB writes on every trigger**: Job execution does not immediately hit DB (except completion).

---

## Future Enhancements

* Retry mechanism for failed jobs.
* Job priority and parallelism.
* Job chaining and dependency graphs.
* UI Dashboard for monitoring jobs.
* Audit logging of job executions.

---

## Maintainers

* Sachin Deoli (Developer and Architect)


---

Chronos — Let your jobs run on time, every time.
