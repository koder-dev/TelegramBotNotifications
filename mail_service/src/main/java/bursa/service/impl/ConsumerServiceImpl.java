package bursa.service.impl;

import bursa.service.ConsumerService;
import bursa.service.MailSenderService;
import bursa.utils.dto.MailParams;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static bursa.model.RabbitQueue.REGISTRATION_MAIL_MESSAGE;

@Service
public class ConsumerServiceImpl implements ConsumerService {
    private MailSenderService mailSenderService;

    public ConsumerServiceImpl(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @Override
    @RabbitListener(queues = REGISTRATION_MAIL_MESSAGE)
    public void consumeRegistrationMail(MailParams mailParams) {
        mailSenderService.send(mailParams);
    }
}
