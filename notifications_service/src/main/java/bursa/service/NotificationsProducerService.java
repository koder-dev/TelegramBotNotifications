package bursa.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface NotificationsProducerService {
    void produceAnswer(SendMessage sendMessage);
    void produceEdit(EditMessageText editMessageText);
    void produceDelete(DeleteMessage deleteMessage);
}
