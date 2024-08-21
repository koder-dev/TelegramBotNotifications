package bursa.service.enums;

public enum TelegramCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

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

    public boolean equals(String command) {
        return this.toString().equals(command);
    }
}
