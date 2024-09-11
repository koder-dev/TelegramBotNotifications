package bursa.service.impl;

import bursa.service.MailSenderService;
import bursa.utils.dto.MailParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static bursa.string.MailTextConst.MAIL_BODY_TEXT;
import static bursa.string.MailTextConst.MAIL_SUBJECT_TEXT;

@Service
public class MailSenderImpl implements MailSenderService {
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${service.activation.uri}")
    private String activationServiceUri;


    public MailSenderImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(MailParams mailParams) {
        String body = getActivationMailBody(mailParams.getId());
        String emailTo = mailParams.getEmailTo();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setSubject(MAIL_SUBJECT_TEXT);
        message.setText(body);
        message.setTo(emailTo);
        mailSender.send(message);
    }

    private String getActivationMailBody(String id) {
        String message = String.format(MAIL_BODY_TEXT, activationServiceUri);
        return message.replace("{id}", id);
    }
}
