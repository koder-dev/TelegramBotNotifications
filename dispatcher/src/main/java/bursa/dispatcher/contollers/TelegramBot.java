package bursa.dispatcher.contollers;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

import static bursa.dispatcher.strings.StringConstants.*;

@Component
@Log4j
@PropertySource("classpath:telegram.properties")
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private TelegramClient telegramClient;
    @Value("${bot.token}")
    private String botToken;
    private final UpdateController updateController;

    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
    }

    @PostConstruct
    public void init() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
        updateController.registerBot(this);
        setCommands();
    }

    private void setCommands() {
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", START_COMMAND_TEXT));
        commands.add(new BotCommand("/cancel", CANCEL_COMMAND_TEXT));
        commands.add(new BotCommand("/registration", REGISTRATION_COMMAND_TEXT));
        commands.add(new BotCommand("/notifications", NOTIFICATIONS_COMMAND_TEXT));
        try {
            telegramClient.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(COMMAND_SETTING_ERROR_TEXT, e);
        }
    }


    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    public void sendAnswerMessage(SendMessage message) throws TelegramApiException {
        telegramClient.execute(message);
    }

    public void sendAnswerMessage(EditMessageText editMessageText) throws TelegramApiException {
        telegramClient.execute(editMessageText);
    }

    public void sendAnswerMessage(DeleteMessage deleteMessage) throws TelegramApiException {
        telegramClient.execute(deleteMessage);
    }

    @Override
    public void consume(Update update) {
        updateController.processUpdate(update);
    }

    public void sendAnswerMessage(EditMessageReplyMarkup editMessageReplyMarkup) throws TelegramApiException {
        telegramClient.execute(editMessageReplyMarkup);
    }
}
