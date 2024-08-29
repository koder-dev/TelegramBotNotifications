package bursa.service;

import bursa.utils.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
