package bursa.service;

import bursa.entities.AppUser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface CommandHandlerService {
    void processCommand(AppUser user, String command, SendMessage.SendMessageBuilder<?, ? extends SendMessage.SendMessageBuilder<?, ?>> messageBuilder);
}
