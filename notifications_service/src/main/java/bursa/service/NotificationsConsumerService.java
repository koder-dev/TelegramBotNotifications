package bursa.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface NotificationsConsumerService {
    void consumeNotificationMessageUpdates(Update update);
    void consumeCallbackQueryUpdates(Update update);
    void consumeNotificationEditTextMessage(Update update);
    void consumeNotificationEditTimeMessage(Update update);
}
