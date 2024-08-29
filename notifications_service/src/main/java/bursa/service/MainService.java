package bursa.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void processNotificationMessage(Update update);
    void processNotificationCallbackQuery(Update update);
}
