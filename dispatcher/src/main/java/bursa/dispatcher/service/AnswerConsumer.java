package bursa.dispatcher.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface AnswerConsumer {
    void consume(SendMessage message);
    void consume(EditMessageText editMessageText);
    void consume(DeleteMessage deleteMessage);
    void consume(EditMessageReplyMarkup editMessageReplyMarkup);
}
