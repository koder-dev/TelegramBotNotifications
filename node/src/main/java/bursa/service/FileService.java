package bursa.service;

import bursa.entities.AppDocument;
import bursa.entities.AppVideo;
import bursa.service.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppVideo processVideo(Message telegramMessage);
    String generateLink(Long id, LinkType linkType);
}
