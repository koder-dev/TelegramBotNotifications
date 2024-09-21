package bursa.configuration;

import bursa.service.quartz.NotificationCleanupJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail notificationCleanupJobDetail() {
        return JobBuilder.newJob(NotificationCleanupJob.class)
                .withIdentity("notificationCleanupJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger notificationCleanupTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(notificationCleanupJobDetail())
                .withIdentity("notificationCleanupTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 0))
                .build();
    }
}
