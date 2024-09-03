package bursa.service.impl;

import bursa.entities.AppUser;
import bursa.service.AppUserService;
import bursa.service.CommandHandlerService;
import bursa.service.ProducerService;
import bursa.service.enums.TelegramCommands;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import static bursa.service.enums.TelegramCommands.*;

@Service
public class CommandHandlerServiceImpl implements CommandHandlerService {
    private final AppUserService appUserService;
    private final ProducerService producerService;

    public CommandHandlerServiceImpl(AppUserService appUserService, ProducerService producerService) {
        this.appUserService = appUserService;
        this.producerService = producerService;
    }

    @Override
    public SendMessage processCommand(AppUser appUser, TelegramCommands command, Long chatId) {
        var message = SendMessage.builder();
        if (REGISTRATION.equals(command)) {
            message.text(appUserService.registerUser(appUser));
        } else if (START.equals(command)) {
            message.text("Будь ласка виберіть одну з команд");
            message.replyMarkup(start());
        } else {
            message.text("Невідома команда,для отримання списку команд введіть /help");
        }
        return message.chatId(chatId).build();
    }


    private ReplyKeyboardMarkup start() {
        KeyboardRow row = new KeyboardRow();
        row.add("/start");
        row.add("/registration");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("/notifications");
        row2.add("/cancel");
        return ReplyKeyboardMarkup.builder().keyboardRow(row).keyboardRow(row2).build();
    }
}
