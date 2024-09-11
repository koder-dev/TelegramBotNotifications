package bursa.dispatcher.contollers;

import bursa.dispatcher.service.UpdateProducer;
import bursa.dispatcher.utils.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static bursa.dispatcher.strings.StringConstants.*;
import static bursa.model.RabbitQueue.*;
import java.util.Objects;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }

    public void registerBot(TelegramBot bot) {
        this.telegramBot = bot;
    }

    public void processUpdate(Update update) {
        if (Objects.isNull(update)) log.debug(UPDATE_NULL_ERROR_TEXT);
        else if (update.hasMessage()) distributeMessageByType(update);
        else if (update.hasCallbackQuery()) processCallbackQuery(update);
        else log.error(UNSUPPORTED_UPDATE_TYPE_TEXT + update);
    }

    public void distributeMessageByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) processTextMessage(update);
        else if (message.hasAudio()) processAudioMessage(update);
        else if (message.hasDocument()) processDocumentMessage(update);
        else if (message.hasVideo()) processVideoMessage(update);
        else if (message.hasPhoto()) processPhotoMessage(update);
        else setUnsupportedMessageTypeView(update);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
    }

    private void processCallbackQuery(Update update) {
        var callbackData = update.getCallbackQuery().getData();
        if (callbackData.startsWith("disk:")) {
            updateProducer.produce(DISC_CALLBACK_QUERY, update);
        }
        updateProducer.produce(CALLBACK_QUERY, update);
    }

    private void processVideoMessage(Update update) {
        updateProducer.produce(VIDEO_MESSAGE_UPDATE, update);
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateAnswerMessage(update, UNSUPPORTED_MESSAGE_TYPE_TEXT);
        setView(sendMessage);
    }


    public void setView(SendMessage sendMessage) {
        try {
            telegramBot.sendAnswerMessage(sendMessage);
        } catch (TelegramApiException e) {
            throw new AmqpRejectAndDontRequeueException("Telegram API error: " + e.getMessage());
        }
    }

    public void setView(EditMessageText editMessageText) {
        try {
            telegramBot.sendAnswerMessage(editMessageText);
        } catch (TelegramApiException e) {
            throw new AmqpRejectAndDontRequeueException("Telegram API error: " + e.getMessage());
        }
    }

    public void setView(DeleteMessage deleteMessage) {
        try {
            telegramBot.sendAnswerMessage(deleteMessage);
        } catch (TelegramApiException e) {
            throw new AmqpRejectAndDontRequeueException("Telegram API error: " + e.getMessage());
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
