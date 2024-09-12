package bursa.dispatcher.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils {
    public SendMessage generateAnswerMessage(Update update, String text) {
        long chat_id = update.getMessage().getChatId();

        return SendMessage // Create a message object
                .builder()
                .chatId(chat_id)
                .text(text)
                .build();
    }
}
