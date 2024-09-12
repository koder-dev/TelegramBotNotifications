package bursa.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ProducerService {
    void producerAnswer(SendMessage sendMessage);
    void produce(String rabbitQueue, Update update);

    void producerEditMarkupAnswer(EditMessageReplyMarkup editMessageReplyMarkup);
}
