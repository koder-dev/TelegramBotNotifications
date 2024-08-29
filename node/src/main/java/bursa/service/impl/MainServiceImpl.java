package bursa.service.impl;

import bursa.entities.AppDocument;
import bursa.entities.AppUser;
import bursa.entities.AppVideo;
import bursa.entity.RawData;
import bursa.exceptions.UploadFileException;
import bursa.repositories.AppUserRepo;
import bursa.repositories.RawDataRepo;
import bursa.service.*;
import bursa.service.enums.LinkType;
import bursa.service.enums.TelegramCommands;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import static bursa.enums.UserState.BASIC_STATE;
import static bursa.enums.UserState.WAIT_FOR_EMAIL;
import static bursa.model.RabbitQueue.NOTIFICATION_MESSAGE_UPDATE;
import static bursa.service.enums.TelegramCommands.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataRepo rawDataRepo;
    private final ProducerService producerService;
    private final AppUserRepo appUserRepo;
    private final FileService fileService;
    private final AppUserService appUserService;
    private final CommandHandlerService commandHandlerService;

    public MainServiceImpl(RawDataRepo rawDataRepo, ProducerService producerService, AppUserRepo appUserRepo, FileService fileService, AppUserService appUserService, CommandHandlerService commandHandlerService) {
        this.rawDataRepo = rawDataRepo;
        this.producerService = producerService;
        this.appUserRepo = appUserRepo;
        this.fileService = fileService;
        this.appUserService = appUserService;
        this.commandHandlerService = commandHandlerService;
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
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getUserState();
        var text = update.getMessage().getText();
        var telegramCommand = TelegramCommands.fromValue(text);
        var chatId = update.getMessage().getChatId();
        var messageBuilder = SendMessage.builder();


        if (CANCEL.equals(telegramCommand)) {
           messageBuilder.text(cancelProcess(appUser));
        } else if (NOTIFICATION.equals(telegramCommand)){
            producerService.produce(NOTIFICATION_MESSAGE_UPDATE, update);
            return;
        } else if (BASIC_STATE.equals(userState)) {
            commandHandlerService.processCommand(appUser, text, messageBuilder);
        } else if (WAIT_FOR_EMAIL.equals(userState)) {
            messageBuilder.text(appUserService.setEmail(appUser, text));
        } else {
            log.error("Unknown state: " + userState);
            messageBuilder.text("Невідома помилка, введіть /cancel та попробуйте пізніше");
        }
        producerService.producerAnswer(messageBuilder.chatId(chatId).build());
    }


    private void sendAnswer(String text, Long charId) {
        var sendMessage = SendMessage.builder().text(text).chatId(charId).build();
        producerService.producerAnswer(sendMessage);
    }

    @Override
    public void processAudioMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }
        //TODO реалізувати зберігання audio
        var answer = "Audio has uploaded.Link for downloading - http:2000";
        sendAnswer(answer, chatId);
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

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }
        try {
            AppDocument appDocument = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(appDocument.getId(), LinkType.GET_DOC);
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
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppVideo appVideo = fileService.processVideo(update.getMessage());
            String link = fileService.generateLink(appVideo.getId(), LinkType.GET_VIDEO);
            var answer = "Video has uploaded.Link for downloading - " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            sendAnswer(ex.getMessage(), chatId);
        }
    }


    private String cancelProcess(AppUser appUser) {
        appUser.setUserState(BASIC_STATE);
        appUserRepo.save(appUser);
        return "Command canceled";
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder().chatId(update.getMessage().getChatId()).text(update.getMessage().getText()).build();
        rawDataRepo.save(rawData);
    }
}
