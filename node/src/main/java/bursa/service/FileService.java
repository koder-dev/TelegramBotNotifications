package bursa.service;

import bursa.entities.*;
import bursa.service.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppVideo processVideo(Message telegramMessage, AppUser appUser);
    String generateLink(Long id, LinkType linkType);

    AppAudio processAudio(Message message, AppUser appUser);
    AppPhoto processPhoto(Message message, AppUser appUser);
}
