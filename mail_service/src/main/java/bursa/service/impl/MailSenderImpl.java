package bursa.service.impl;

import bursa.service.MailSenderService;
import bursa.utils.dto.MailParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
        String subject = "Account activation in telegram bot";
        String body = getActivationMailBody(mailParams.getId());
        String emailTo = mailParams.getEmailTo();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setSubject(subject);
        message.setText(body);
        message.setTo(emailTo);
        mailSender.send(message);
    }

    private String getActivationMailBody(String id) {
        String message = String.format("Activate your by this link: \n%s", activationServiceUri);
        return message.replace("{id}", id);
    }
}
