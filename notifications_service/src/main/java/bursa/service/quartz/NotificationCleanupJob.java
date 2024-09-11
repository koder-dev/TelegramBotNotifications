package bursa.service.quartz;

import bursa.repositories.AppNotificationsRepo;
import jakarta.transaction.Transactional;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class NotificationCleanupJob implements Job {
    private AppNotificationsRepo appNotificationsRepo;

    public NotificationCleanupJob(AppNotificationsRepo appNotificationsRepo) {
        this.appNotificationsRepo = appNotificationsRepo;
    }

    @Override
    @Transactional
    public void execute(JobExecutionContext context) {
        LocalDateTime now = LocalDateTime.now();
        appNotificationsRepo.deleteByNotifyTimeBefore(now);
    }
}

