package bursa.service.quartz;

import bursa.entities.AppNotification;
import bursa.repositories.AppNotificationsRepo;
import bursa.service.QuartSchedulerService;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class QuartzSchedulerServiceImpl implements QuartSchedulerService {

    private Scheduler scheduler;
    private AppNotificationsRepo appNotificationsRepo;

    public QuartzSchedulerServiceImpl(Scheduler scheduler, AppNotificationsRepo appNotificationsRepo) {
        this.scheduler = scheduler;
        this.appNotificationsRepo = appNotificationsRepo;
    }

    @Override
    public void scheduleNotification(LocalDateTime startTime, Long chatId, AppNotification appNotification) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(NotificationJob.class)
                    .withIdentity("notificationJob:" + appNotification.getId(), "notificationGroup")
                    .usingJobData("chatId", chatId)
                    .usingJobData("notificationId", appNotification.getId())
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("notificationTrigger" + appNotification.getId(), "notificationGroup")
                    .startAt(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelNotification(Long notificationId) {
        try {
            JobKey jobKey = new JobKey("notificationJob:" + notificationId, "notificationGroup");
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
