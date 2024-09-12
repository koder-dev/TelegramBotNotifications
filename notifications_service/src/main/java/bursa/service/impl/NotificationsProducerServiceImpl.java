package bursa.service.impl;

import bursa.service.NotificationsProducerService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import static bursa.model.RabbitQueue.*;

@Service
public class NotificationsProducerServiceImpl implements NotificationsProducerService {
    private final RabbitTemplate rabbitTemplate;

    public NotificationsProducerServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produceAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    public void produceEdit(EditMessageText editMessageText) {
        rabbitTemplate.convertAndSend(EDIT_MESSAGE, editMessageText);
    }

    public void produceDelete(DeleteMessage deleteMessage) {
        rabbitTemplate.convertAndSend(DELETE_MESSAGE, deleteMessage);
    }
}
