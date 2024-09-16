package bursa.service.impl;

import bursa.service.ConsumerService;
import bursa.service.MainService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static bursa.model.RabbitQueue.*;
import static bursa.model.RabbitQueue.DISC_CALLBACK_QUERY;

@Log4j2
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumerTextMessageUpdates(Update update) {
        log.debug("NODE: text message is received");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = VIDEO_MESSAGE_UPDATE)
    public void consumerVideoMessageUpdates(Update update) {
        log.debug("NODE: video message is received");
        mainService.processVideoMessage(update);
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumerPhotoMessageUpdates(Update update) {
        mainService.processPhotoMessage(update);
    }

    @Override
    @RabbitListener(queues = AUDIO_MESSAGE_UPDATE)
    public void consumerAudioMessageUpdates(Update update) {
        log.debug("NODE: audio message is received");
        mainService.processAudioMessage(update);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumerDocMessageUpdates(Update update) {
        log.debug("NODE: doc message is received");
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = DISC_CALLBACK_QUERY)
    public void consumerDiscCallbackQueryUpdates(Update update) {
        mainService.processCallbackQueryMessage(update);
    }
}
