package bursa.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    void consumeNotificationMessageUpdates(Update update);
    void consumeCallbackQueryUpdates(Update update);
}
