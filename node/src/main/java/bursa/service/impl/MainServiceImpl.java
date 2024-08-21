package bursa.service.impl;

import bursa.entities.AppDocument;
import bursa.entities.AppUser;
import bursa.entities.AppVideo;
import bursa.entity.RawData;
import bursa.exceptions.UploadFileException;
import bursa.repositories.AppUserRepo;
import bursa.repositories.RawDataRepo;
import bursa.service.FileService;
import bursa.service.MainService;
import bursa.service.ProducerService;
import bursa.service.enums.LinkType;
import bursa.service.enums.TelegramCommands;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static bursa.enums.UserState.BASIC_STATE;
import static bursa.enums.UserState.WAIT_FOR_EMAIL;
import static bursa.service.enums.TelegramCommands.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataRepo rawDataRepo;
    private final ProducerService producerService;
    private final AppUserRepo appUserRepo;
    private final FileService fileService;

    public MainServiceImpl(RawDataRepo rawDataRepo, ProducerService producerService, AppUserRepo appUserRepo, FileService fileService) {
        this.rawDataRepo = rawDataRepo;
        this.producerService = producerService;
        this.appUserRepo = appUserRepo;
        this.fileService = fileService;
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        return appUserRepo.findAppUserByTelegramUserId(telegramUser.getId()).orElseGet(() -> {
            AppUser appUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO поміняти значення по замовчуванню після додавання регістрації
                    .isActive(true)
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
        var output = "";

        if (CANCEL.equals(telegramCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL.equals(userState)) {
            //TODO додати оброботку емейл
        } else {
            log.error("Unknown state: " + userState);
            output = "Невідома помилка, введіть /cancel та попробуйте пізніше";
        }
        Long chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(output).build();
        producerService.producerAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String command) {
        if (REGISTRATION.equals(command)) {
            //TODO добавити регістрацію
            return "Поки що недоступно";
        } else if (HELP.equals(command)) {
            return help();
        } else if (START.equals(command)) {
            return "Добрий день, для отримання списку команд введіть /help";
        } else {
            return "Невідома команда,для отримання списку команд введіть /help";
        }
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

    private String help() {
        return "Cписок доступних команд:\n"
                + "/cancel - відміна поточної дії"
                + "/registration - реєстрація в боті";
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
