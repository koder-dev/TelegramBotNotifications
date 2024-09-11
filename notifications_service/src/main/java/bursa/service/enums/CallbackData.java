package bursa.service.enums;

public enum CallbackData {
    CREATE_NEW_NOTIFICATION("create-new-notification"),
    SHOW_ALL_NOTIFICATIONS("show-all-notifications"),
    EDIT_NOTIFICATION("edit-notification"),
    EDIT_NOTIFICATION_TIME("edit-notification-time"),
    EDIT_NOTIFICATION_TEXT("edit-notification-text"),
    DELETE_NOTIFICATION("delete-notification"),
    BACK_TO_MAIN_MENU("back-to-main-menu"),
    BACK_TO_NOTIFICATION_MENU("back-to-notification-menu"),
    REPEAT_NOTIFICATION_AFTER("repeat-notification-after");

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
