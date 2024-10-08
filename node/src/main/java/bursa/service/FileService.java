package bursa.service;

import bursa.entities.*;
import bursa.exceptions.IncorrectMediaClassException;
import bursa.exceptions.UploadFileException;
import bursa.service.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage, AppUser appUser) throws UploadFileException, IncorrectMediaClassException;
    AppVideo processVideo(Message telegramMessage, AppUser appUser) throws UploadFileException, IncorrectMediaClassException;
    String generateLink(Long id, LinkType linkType);

    AppAudio processAudio(Message message, AppUser appUser) throws UploadFileException, IncorrectMediaClassException;
    AppPhoto processPhoto(Message message, AppUser appUser) throws UploadFileException, IncorrectMediaClassException;
}
