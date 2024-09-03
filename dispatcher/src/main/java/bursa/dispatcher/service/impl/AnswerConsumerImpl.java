package bursa.dispatcher.service.impl;

import bursa.dispatcher.contollers.UpdateController;
import bursa.dispatcher.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import static bursa.model.RabbitQueue.*;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage message) {
        updateController.setView(message);
    }

    @Override
    @RabbitListener(queues = EDIT_MESSAGE)
    public void consume(EditMessageText editMessageText) {
        updateController.setView(editMessageText);
    }

    @Override
    @RabbitListener(queues = DELETE_MESSAGE)
    public void consume(DeleteMessage deleteMessage) {
        updateController.setView(deleteMessage);
    }
}
