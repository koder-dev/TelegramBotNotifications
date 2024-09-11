package bursa.service.impl;

import bursa.entities.*;
import bursa.exceptions.UploadFileException;
import bursa.repositories.*;
import bursa.service.*;
import bursa.service.enums.LinkType;
import bursa.service.enums.TelegramCommands;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static bursa.enums.UserState.*;
import static bursa.model.RabbitQueue.*;
import static bursa.service.enums.TelegramCommands.*;
import static bursa.service.strings.NodeModuleStringConstants.CANCEL_COMMAND_TEXT;
import static bursa.service.strings.NodeModuleStringConstants.UNKNOWN_USER_STATE_ERROR_TEXT;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final ProducerService producerService;
    private final AppUserRepo appUserRepo;
    private final FileService fileService;
    private final AppUserService appUserService;
    private final CommandHandlerService commandHandlerService;
    private final AppVideoRepo appVideoRepo;
    private final AppDocumentRepo appDocumentRepo;
    private final AppAudioRepo appAudioRepo;
    private final AppPhotoRepo appPhotoRepo;

    public MainServiceImpl(ProducerService producerService, AppUserRepo appUserRepo, FileService fileService, AppUserService appUserService, CommandHandlerService commandHandlerService, AppVideoRepo appVideoRepo, AppDocumentRepo appDocumentRepo, AppAudioRepo appAudioRepo, AppPhotoRepo appPhotoRepo) {
        this.producerService = producerService;
        this.appUserRepo = appUserRepo;
        this.fileService = fileService;
        this.appUserService = appUserService;
        this.commandHandlerService = commandHandlerService;
        this.appVideoRepo = appVideoRepo;
        this.appDocumentRepo = appDocumentRepo;
        this.appAudioRepo = appAudioRepo;
        this.appPhotoRepo = appPhotoRepo;
    }

    @Override
    public void processTextMessage(Update update) {
        var telegramCommand = TelegramCommands.fromValue(update.getMessage().getText());
        var user = findOrSaveAppUser(update);

        if (CANCEL.equals(telegramCommand)) {
            sendAnswer(cancelProcess(user), update.getMessage().getChatId());
        } else {
            handleUserState(update, user, telegramCommand);
        }
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setUserState(BASIC_STATE);
        appUserRepo.save(appUser);
        return CANCEL_COMMAND_TEXT;
    }

    private void handleUserState(Update update, AppUser user, TelegramCommands telegramCommand) {
        var userState = user.getUserState();
        var chatId = update.getMessage().getChatId();
        var text = update.getMessage().getText();
        if (NOTIFICATIONS_STATE.equals(userState) || NOTIFICATIONS.equals(telegramCommand)) producerService.produce(NOTIFICATION_MESSAGE_UPDATE, update);
        else if (NOTIFICATION_EDIT_TIME_STATE.equals(userState)) producerService.produce(NOTIFICATION_EDIT_TIME_MESSAGE, update);
        else if (NOTIFICATION_EDIT_TEXT_STATE.equals(userState)) producerService.produce(NOTIFICATION_EDIT_TEXT_MESSAGE, update);
        else if (WAIT_FOR_EMAIL.equals(userState)) sendAnswer(appUserService.setEmail(user, text), chatId);
        else if (BASIC_STATE.equals(userState)) sendAnswer(commandHandlerService.processCommand(user, telegramCommand, chatId));
        else if (DISC_STATE.equals(userState)) sendAnswer(commandHandlerService.processDiscCommand(user, telegramCommand, chatId));
        else sendAnswer(UNKNOWN_USER_STATE_ERROR_TEXT, chatId);
    }

    private void sendAnswer(SendMessage message) {
        producerService.producerAnswer(message);
    }

    private void sendAnswer(String text, Long charId) {
        var sendMessage = SendMessage.builder().text(text).chatId(charId).build();
        producerService.producerAnswer(sendMessage);
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        return appUserRepo.findByTelegramUserId(telegramUser.getId()).orElseGet(() -> {
            AppUser appUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .userState(BASIC_STATE)
                    .build();
            return appUserRepo.save(appUser);
        });
    }

    @Override
    public void processAudioMessage(Update update) {
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppAudio audio = fileService.processAudio(update.getMessage(), appUser);
            String link = fileService.generateLink(audio.getId(), LinkType.GET_AUDIO);
            audio.setDownloadLink(link);
            appAudioRepo.save(audio);
            var answer = "Audio has uploaded.Link for downloading - " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "Вибачте сатлася помилка при заванатажені документа спробуйте пізінше";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage(), appUser);
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            photo.setDownloadLink(link);
            appPhotoRepo.save(photo);
            var answer = "Photo has uploaded.Link for downloading - " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "Вибачте сатлася помилка при заванатажені документа спробуйте пізінше";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processDocMessage(Update update) {
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }
        try {
            AppDocument appDocument = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(appDocument.getId(), LinkType.GET_DOC);
            appDocument.setDownloadLink(link);
            appDocumentRepo.save(appDocument);
            var answer = "Document has uploaded.Link for downloading - " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "Вибачте сатлася помилка при заванатажені документа спробуйте пізінше";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processVideoMessage(Update update) {
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppVideo appVideo = fileService.processVideo(update.getMessage(), appUser);
            String link = fileService.generateLink(appVideo.getId(), LinkType.GET_VIDEO);
            appVideo.setDownloadLink(link);
            appVideoRepo.save(appVideo);
            var answer = "Video has uploaded.Link for downloading - " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            sendAnswer(ex.getMessage(), chatId);
        }
    }

    private boolean isNotAllowedToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getUserState();
        if (!appUser.getIsActive()) {
            var error = "Зареєструйтеся або активуйте аккаунт";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Відмініть команду за допомогою /cancel";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }


}
