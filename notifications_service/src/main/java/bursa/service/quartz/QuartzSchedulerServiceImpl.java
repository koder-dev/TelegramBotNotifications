package bursa.service.quartz;

import bursa.entities.AppNotification;
import bursa.service.QuartSchedulerService;
import lombok.extern.log4j.Log4j2;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static bursa.strings.TelegramTextResponses.*;

@Log4j2
@Service
public class QuartzSchedulerServiceImpl implements QuartSchedulerService {

    private final Scheduler scheduler;

    public QuartzSchedulerServiceImpl(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void scheduleNotification(LocalDateTime startTime, Long chatId, AppNotification appNotification) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(NotificationJob.class)
                    .withIdentity(JOBKEY_NAME_NOTIFICATION_JOB_TEXT + appNotification.getId(), JOBKEY_NOTIFICATION_GROUP_TEXT)
                    .usingJobData(JOB_DATA_CHAT_ID_TEXT, chatId)
                    .usingJobData(JOB_DATA_NOTIFICATION_ID_TEXT, appNotification.getId())
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(JOB_TRIGGER_NOTIFICATION_TEXT + appNotification.getId(), JOBKEY_NOTIFICATION_GROUP_TEXT)
                    .startAt(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error(ERROR_SCHEDULING_NOTIFICATION_TEXT, e);
        }
    }

    @Override
    public void cancelNotification(Long notificationId) {
        try {
            JobKey jobKey = new JobKey(JOBKEY_NAME_NOTIFICATION_JOB_TEXT + notificationId, JOBKEY_NOTIFICATION_GROUP_TEXT);
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error(ERROR_CANCELING_SCHEDULED_NOTIFICATION_TEXT, e);
        }
    }
}
