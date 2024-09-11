package bursa.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void processTextMessage(Update update);
    void processAudioMessage(Update update);
    void processDocMessage(Update update);
    void processVideoMessage(Update update);
    void processPhotoMessage(Update update);
}
