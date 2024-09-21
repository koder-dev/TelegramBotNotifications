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

import static bursa.strings.TelegramTextResponses.*;

@Component
public class NotificationJob implements Job {

    private final NotificationsProducerService notificationsProducerService;
    private final AppNotificationsRepo appNotificationsRepo;

    public NotificationJob(NotificationsProducerService notificationsProducerService, AppNotificationsRepo appNotificationsRepo) {
        this.notificationsProducerService = notificationsProducerService;
        this.appNotificationsRepo = appNotificationsRepo;
    }

    @Override
    public void execute(JobExecutionContext context) {
        Long chatId = context.getJobDetail().getJobDataMap().getLong(JOB_DATA_CHAT_ID_TEXT);
        Long notificationId = context.getJobDetail().getJobDataMap().getLong(JOB_DATA_NOTIFICATION_ID_TEXT);

        Optional<AppNotification> optionalAppNotification = appNotificationsRepo.findById(notificationId);
        if (optionalAppNotification.isEmpty()) return;
        var appNotification = optionalAppNotification.get();
        var markup = RenderUiTools.renderRepeatNotificationButtons(appNotification.getId());
        var text = String.format(NOTIFICATION_TEMPLATE_TEXT, appNotification.getNotifyText());
        var message = SendMessage.builder().text(text).chatId(chatId).replyMarkup(markup).build();
        notificationsProducerService.produceAnswer(message);
    }
}
