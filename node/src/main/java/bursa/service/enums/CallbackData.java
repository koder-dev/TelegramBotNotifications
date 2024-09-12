package bursa.service.enums;

public enum CallbackData {
    NEXT("disc:next"),
    PREV("disc:prev");

    private final String data;

    CallbackData(String command) {
        this.data = command;
    }

    public static CallbackData fromValue(String text) {
        for (CallbackData callbackData : CallbackData.values()) {
            if (callbackData.toString().equalsIgnoreCase(text)) {
                return callbackData;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return data;
    }
}
