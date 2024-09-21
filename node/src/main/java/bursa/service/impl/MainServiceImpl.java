package bursa.service.impl;

import bursa.entities.*;
import bursa.exceptions.IncorrectMediaClassException;
import bursa.exceptions.NotAllowedToSendContentException;
import bursa.exceptions.UploadFileException;
import bursa.repositories.*;
import bursa.service.*;
import bursa.service.enums.LinkType;
import bursa.service.enums.TelegramCommands;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;

import static bursa.enums.UserState.*;
import static bursa.model.RabbitQueue.*;
import static bursa.service.enums.LinkType.*;
import static bursa.service.enums.TelegramCommands.*;
import static bursa.service.strings.NodeModuleStringConstants.*;

@Log4j2
@Service
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
        var telegramCommand = fromValue(update.getMessage().getText());
        var user = findOrSaveAppUser(update);

        if (CANCEL.equals(telegramCommand)) {
            sendAnswer(commandHandlerService.cancelProcess(user, update.getMessage().getChatId()));
        } else {
            handleUserState(update, user, telegramCommand);
        }
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


    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.hasCallbackQuery() ? update.getCallbackQuery().getFrom() : update.getMessage().getFrom();
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
    public void processCallbackQueryMessage(Update update) {
        var user = findOrSaveAppUser(update);
        sendEditMarkupAnswer(commandHandlerService.processNavigationCallbackQuery(user, update));
    }


    private AppMedia processMediaMessage(Update update, LinkType linkType) {
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        String answer = null;
        try {
            if (Boolean.FALSE.equals(appUser.getIsActive())) {
                throw new NotAllowedToSendContentException(NOT_REGISTERED_ACCOUNT_TEXT);
            }

            AppMedia media;
            if (linkType.equals(GET_PHOTO)) media = fileService.processPhoto(update.getMessage(), appUser);
            else if (linkType.equals(GET_VIDEO)) media = fileService.processVideo(update.getMessage(), appUser);
            else if (linkType.equals(GET_AUDIO)) media = fileService.processAudio(update.getMessage(), appUser);
            else if (linkType.equals(GET_DOC)) media = fileService.processDoc(update.getMessage(), appUser);
            else throw new IncorrectMediaClassException(INCORRECT_MEDIA_CLASS_TEXT);
            String link = fileService.generateLink(media.getId(), linkType);
            media.setDownloadLink(link);
            answer = MEDIAL_HAS_UPLOADED_TEXT + link;
            return media;
        } catch (UploadFileException | IncorrectMediaClassException | NotAllowedToSendContentException ex) {
            log.error(ex);
            answer = ex.getMessage();
        } finally {
            if (Objects.isNull(answer)) answer = UNKNOWN_USER_STATE_ERROR_TEXT;
            sendAnswer(answer, chatId);
        }
        return null;
    }

    @Override
    public void processAudioMessage(Update update) {
        AppMedia audio = processMediaMessage(update, GET_AUDIO);
        if (Objects.nonNull(audio)) {
            appAudioRepo.save((AppAudio) audio);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        AppMedia photo = processMediaMessage(update, GET_PHOTO);
        if (Objects.nonNull(photo)) {
            appPhotoRepo.save((AppPhoto) photo);
        }
    }

    @Override
    public void processDocMessage(Update update) {
        AppMedia doc = processMediaMessage(update, GET_DOC);
        if (Objects.nonNull(doc)) {
            appDocumentRepo.save((AppDocument) doc);
        }
    }

    @Override
    public void processVideoMessage(Update update) {
        AppMedia video = processMediaMessage(update, GET_VIDEO);
        if (Objects.nonNull(video)) {
            appVideoRepo.save((AppVideo) video);
        }
    }

    private void sendAnswer(SendMessage message) {
        producerService.producerAnswer(message);
    }

    private void sendAnswer(String text, Long charId) {
        var sendMessage = SendMessage.builder().text(text).chatId(charId).build();
        producerService.producerAnswer(sendMessage);
    }

    private void sendEditMarkupAnswer(EditMessageReplyMarkup editMessageReplyMarkup) {
        producerService.producerEditMarkupAnswer(editMessageReplyMarkup);
    }


}
