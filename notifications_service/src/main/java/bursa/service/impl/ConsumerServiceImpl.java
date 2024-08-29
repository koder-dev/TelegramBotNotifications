package bursa.service.impl;

import bursa.service.ConsumerService;
import bursa.service.MainService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static bursa.model.RabbitQueue.NOTIFICATION_MESSAGE_UPDATE;

@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = NOTIFICATION_MESSAGE_UPDATE)
    public void consumeNotificationMessageUpdates(Update update) {
        mainService.processNotificationMessage(update);
    }

    @Override
    public void consumeCallbackQueryUpdates(Update update) {
        mainService.processNotificationMessage(update);
    }
}
