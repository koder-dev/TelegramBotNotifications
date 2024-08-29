package bursa.service.impl;

import bursa.entities.AppUser;
import bursa.service.AppUserService;
import bursa.service.CommandHandlerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import static bursa.service.enums.TelegramCommands.*;

@Service
public class CommandHandlerServiceImpl implements CommandHandlerService {
    private final AppUserService appUserService;

    public CommandHandlerServiceImpl(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @Override
    public void processCommand(AppUser appUser, String command, SendMessage.SendMessageBuilder<?, ? extends SendMessage.SendMessageBuilder<?, ?>> messageBuilder) {
        if (REGISTRATION.equals(command)) {
            messageBuilder.text(appUserService.registerUser(appUser));
        } else if (HELP.equals(command)) {
            messageBuilder.text(help());
        } else if (START.equals(command)) {
            messageBuilder.text("Будь ласка виберіть одну з команд");
            messageBuilder.replyMarkup(start());
        } else {
            messageBuilder.text("Невідома команда,для отримання списку команд введіть /help");
        }
    }

    private String help() {
        return """
                Cписок доступних команд:
                cancel - відміна поточної дії
                registration - реєстрація в боті
                """;
    }

    private ReplyKeyboardMarkup start() {
        KeyboardRow row = new KeyboardRow();
        row.add("/cancel");
        row.add("/registration");
        return ReplyKeyboardMarkup.builder().keyboardRow(row).build();
    }
}
