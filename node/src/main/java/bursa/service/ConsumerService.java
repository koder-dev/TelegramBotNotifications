package bursa.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    void consumerTextMessageUpdates(Update update);
    void consumerVideoMessageUpdates(Update update);
    void consumerAudioMessageUpdates(Update update);
    void consumerDocMessageUpdates(Update update);
}
