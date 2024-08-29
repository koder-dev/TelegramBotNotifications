package bursa.service.enums;

public enum CallbackData {
    CREATE_NEW_NOTIFICATION("create-new-notification"),
    SHOW_ALL_NOTIFICATIONS("show-all-notifications");

    private final String value;

    CallbackData(String callbackData) {
        this.value = callbackData;
    }

    public CallbackData fromValue(String text) {
        for (CallbackData cd : CallbackData.values()) {
            if (cd.value.equals(text)) {
                return cd;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
