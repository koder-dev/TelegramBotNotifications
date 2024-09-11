package bursa.service.quartz;

import bursa.entities.AppNotification;
import bursa.repositories.AppNotificationsRepo;
import bursa.service.NotificationsProducerService;
import bursa.utils.RenderUiTools;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

@Component
public class NotificationJob implements Job {

    private NotificationsProducerService notificationsProducerService;
    private AppNotificationsRepo appNotificationsRepo;

    public NotificationJob(NotificationsProducerService notificationsProducerService, AppNotificationsRepo appNotificationsRepo) {
        this.notificationsProducerService = notificationsProducerService;
        this.appNotificationsRepo = appNotificationsRepo;
    }

    @Override
    public void execute(JobExecutionContext context) {
        Long chatId = context.getJobDetail().getJobDataMap().getLong("chatId");
        Long notificationId = context.getJobDetail().getJobDataMap().getLong("notificationId");

        Optional<AppNotification> optionalAppNotification = appNotificationsRepo.findById(notificationId);
        if (optionalAppNotification.isEmpty()) return;
        var appNotification = optionalAppNotification.get();
        var markup = RenderUiTools.renderRepeatNotificationButtons(appNotification.getId());
        var message = SendMessage.builder().text(appNotification.getNotifyText()).chatId(chatId).replyMarkup(markup).build();
        notificationsProducerService.produceAnswer(message);
    }
}
