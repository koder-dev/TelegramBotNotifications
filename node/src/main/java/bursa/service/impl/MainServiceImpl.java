package bursa.service.impl;

import bursa.entities.AppUser;
import bursa.entity.RawData;
import bursa.repositories.AppUserRepo;
import bursa.repositories.RawDataRepo;
import bursa.service.MainService;
import bursa.service.ProducerService;
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

    public MainServiceImpl(RawDataRepo rawDataRepo, ProducerService producerService, AppUserRepo appUserRepo) {
        this.rawDataRepo = rawDataRepo;
        this.producerService = producerService;
        this.appUserRepo = appUserRepo;
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
        var output = "";

        if (CANCEL.equals(text)) {
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
