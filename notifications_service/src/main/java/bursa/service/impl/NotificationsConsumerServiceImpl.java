package bursa.service.impl;

import bursa.service.NotificationsConsumerService;
import bursa.service.MainService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static bursa.model.RabbitQueue.*;
import static bursa.model.RabbitQueue.NOTIFICATION_EDIT_TIME_MESSAGE;

@Service
public class NotificationsConsumerServiceImpl implements NotificationsConsumerService {
    private final MainService mainService;

    public NotificationsConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = NOTIFICATION_MESSAGE_UPDATE)
    public void consumeNotificationMessageUpdates(Update update) {
        mainService.processNotificationMessage(update);
    }

    @Override
    @RabbitListener(queues = CALLBACK_QUERY)
    public void consumeCallbackQueryUpdates(Update update) {
        mainService.processNotificationCallbackQuery(update);
    }

    @Override
    @RabbitListener(queues = NOTIFICATION_EDIT_TEXT_MESSAGE)
    public void consumeNotificationEditTextMessage(Update update) {
        mainService.processNotificationEditText(update);
    }

    @Override
    @RabbitListener(queues = NOTIFICATION_EDIT_TIME_MESSAGE)
    public void consumeNotificationEditTimeMessage(Update update) {
        mainService.processNotificationEditTime(update);
    }
}
