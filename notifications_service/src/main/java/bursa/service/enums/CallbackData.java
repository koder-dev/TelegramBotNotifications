package bursa.service.enums;

public enum CallbackData {
    CREATE_NEW_NOTIFICATION("notification:create-new"),
    SHOW_ALL_NOTIFICATIONS("notification:show-all"),
    EDIT_NOTIFICATION("notification:edit"),
    EDIT_NOTIFICATION_TIME("notification:edit-time"),
    EDIT_NOTIFICATION_TEXT("notification:edit-text"),
    DELETE_NOTIFICATION("notification:delete"),
    BACK_TO_MAIN_MENU("notification:back-to-main-menu"),
    BACK_TO_NOTIFICATION_MENU("notification:back-to-menu"),
    REPEAT_NOTIFICATION_AFTER("notification:repeat-after");

    private final String value;

    CallbackData(String callbackData) {
        this.value = callbackData;
    }

    public static CallbackData fromValue(String text) {
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
