package bursa.service;

import bursa.entities.AppDocument;
import bursa.entities.AppVideo;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppVideo processVideo(Message telegramMessage);
}
