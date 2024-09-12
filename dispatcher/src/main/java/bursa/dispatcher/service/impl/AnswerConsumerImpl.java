package bursa.dispatcher.service.impl;

import bursa.dispatcher.contollers.UpdateController;
import bursa.dispatcher.service.AnswerConsumer;
import org.springframework.stereotype.Service;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

}
