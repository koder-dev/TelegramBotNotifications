package bursa.service.enums;

import static bursa.service.strings.NodeModuleStringConstants.*;

public enum TelegramCommands {
    HELP(HELP_COMMAND_TEXT),
    REGISTRATION(REGISTRATION_COMMAND_TEXT),
    NOTIFICATIONS(NOTIFICATION_COMMAND_TEXT),
    SETTINGS(SETTINGS_COMMAND_TEXT),
    CANCEL(CANCEL_COMMAND_TEXT),
    START(START_COMMAND_TEXT),
    BACK(BACK_COMMAND_TEXT),
    DISC(DISC_COMMAND_TEXT),
    VIDEOS(VIDEOS_COMMAND_TEXT),
    PHOTOS(PHOTOS_COMMAND_TEXT),
    DOCS(DOCS_COMMAND_TEXT),
    AUDIO(AUDIO_COMMAND_TEXT);

    private final String command;

    TelegramCommands(String command) {
        this.command = command;
    }

    public static TelegramCommands fromValue(String text) {
        for (TelegramCommands command : TelegramCommands.values()) {
            if (command.toString().equalsIgnoreCase(text)) {
                return command;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return command;
    }

}
