
// ================================================================
// FILE 4: SchedulerConfig.java
// ================================================================
package com.fintrix.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SchedulerConfig
 *
 * Activates @Scheduled jobs in the jobs/ package.
 * Without this, @Scheduled annotations are ignored.
 * (Also annotated on FintrixApplication — both work.)
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
    /*
     * Default thread pool for @Scheduled = 1 thread.
     * If jobs overlap, second waits for first to finish.
     *
     * For production with many jobs, configure ThreadPoolTaskScheduler:
     * ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
     * scheduler.setPoolSize(5);
     */
}

