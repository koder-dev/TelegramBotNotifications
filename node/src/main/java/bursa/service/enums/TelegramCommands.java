package bursa.service.enums;

public enum TelegramCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    NOTIFICATIONS("/notifications"),
    CANCEL("/cancel"),
    START("/start"),
    SEARCH("/search"),
    BACK("/back"),
    DISC("/disc"),
    VIDEOS("/videos"),
    PHOTOS("/photos"),
    DOCS("/docs"),
    AUDIO("/audio");

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
