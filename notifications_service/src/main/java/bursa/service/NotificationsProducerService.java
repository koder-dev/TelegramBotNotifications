package bursa.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface NotificationsProducerService {
    void produceAnswer(SendMessage sendMessage);
}
