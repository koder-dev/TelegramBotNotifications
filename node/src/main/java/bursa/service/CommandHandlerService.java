package bursa.service;

import bursa.entities.AppUser;
import bursa.service.enums.TelegramCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface CommandHandlerService {
    SendMessage processCommand(AppUser user, TelegramCommands command, Long chatId);

    SendMessage processDiscCommand(AppUser user, TelegramCommands telegramCommand, Long chatId);
}
