package bursa.dispatcher.contollers;

import bursa.dispatcher.service.UpdateProducer;
import bursa.dispatcher.utils.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import static bursa.model.RabbitQueue.*;
import java.util.Objects;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private MessageUtils messageUtils;
    private UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }

    public void registerBot(TelegramBot bot) {
        this.telegramBot = bot;
    }

    public void processUpdate(Update update) {
        if (Objects.isNull(update)) log.debug("Update is null");
        if (update.hasMessage()) distributeMessageByType(update);
        else log.error("Received unsupported update message" + update);
    }

    public void distributeMessageByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) processTextMessage(update);
        else if (message.hasAudio()) processAudioMessage(update);
        else if (message.hasDocument()) processDocumentMessage(update);
        else if (message.hasVideo()) processVideoMessage(update);
        else if (update.hasCallbackQuery()) processCallbackQuery(update);
        else setUnsupportedMessageTypeView(update);
    }

    private void processCallbackQuery(Update update) {
        updateProducer.produce(CALLBACK_QUERY, update);
    }

    private void processVideoMessage(Update update) {
        updateProducer.produce(VIDEO_MESSAGE_UPDATE, update);
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateAnswerMessage(update, "Unsupported message type");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        try {
            telegramBot.sendAnswerMessage(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void processDocumentMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
    }

    private void processAudioMessage(Update update) {
        updateProducer.produce(AUDIO_MESSAGE_UPDATE, update);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }
}
