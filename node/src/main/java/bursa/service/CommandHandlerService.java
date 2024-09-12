package bursa.service;

import bursa.entities.AppUser;
import bursa.service.enums.TelegramCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandlerService {
    SendMessage processCommand(AppUser user, TelegramCommands command, Long chatId);

    SendMessage processDiscCommand(AppUser user, TelegramCommands telegramCommand, Long chatId);

    SendMessage cancelProcess(AppUser user, Long chatId);

    EditMessageReplyMarkup processCallbackQuery(AppUser user, Update update);
}
