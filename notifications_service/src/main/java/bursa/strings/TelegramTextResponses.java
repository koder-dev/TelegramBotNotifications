package bursa.strings;

public class TelegramTextResponses {
    public static final String CREATE_NEW_NOTIFICATION_BTN_TEXT = "Нове нагадування";
    public static final String SHOW_ALL_NOTIFICATION_BTN_TEXT = "Список нагадувань";
    public static final String WELCOME_MESSAGE_TEXT = "Вітаю, ви в меню нагадувань! Тут ви можете створити нагадування просто ввівши текст та дату.";
    public static final String NOTIFICATION_SUCCESSFULLY_CREATED_TEMPLATE_TEXT = "Нагадування успішне створене!\n\n%s\n\n(%s)";
    public static final String NEW_DESCRIPTION_EDIT_MESSAGE_TEXT = "Введіть новий опис для нагадування";
    public static final String NEW_TIME_EDIT_MESSAGE_TEXT = "Введіть новий час для нагадування";
    public static final String NOTIFICATION_DESCRIPTION_SUCCESSFULLY_CHANGED_TEXT = "Ви успішно змінили опис нагадування";
    public static final String NOTIFICATION_TIME_SUCCESSFULLY_CHANGED_TEXT = "Ви успішно змінили час нагадування";
    public static final String NOTIFICATION_SUCCESSFULLY_DELETED_TEXT = "Ви успішно видалили нагадування";
    public static final String INTERNAL_SERVER_ERROR_TEXT = "Внутрішня помилка серверу введіть /cancel, щоб скинути попередню команду!";
    public static final String EDIT_TEMPLATE_TEXT = "%s\n\nНаступне нагадування буде о %s";
    public static final String NOTIFICATION_LIST_MESSAGE_TEXT = "Виберіть нагадування для редагування!";
    public static final String NO_NOTIFICATION_LIST_MESSAGE_TEXT = "У вас немає нагадувань!";
    public static final String CREATE_NEW_NOTIFICATION_MESSAGE_TEXT = "Будь ласка, введіть текст нагадування і час.\nПриклади:\n-Кава з другом завтра о 17:00\n-Вечірка на 12.12.2024 14:00 або 14.00\n-Прививка  субота 12.30";
    public static final String CHANGE_TEXT_BTN_TEXT = "Змінити опис";
    public static final String CHANGE_TIME_BTN_TEXT = "Змінити час";
    public static final String DELETE_BTN_TEXT = "Видалити";
    public static final String JOBKEY_NOTIFICATION_GROUP_TEXT = "notificationGroup";
    public static final String JOBKEY_NAME_NOTIFICATION_JOB_TEXT = "notificationJob:";
    public static final String JOB_DATA_CHAT_ID_TEXT = "chatId";
    public static final String JOB_DATA_NOTIFICATION_ID_TEXT = "notificationId";
    public static final String JOB_TRIGGER_NOTIFICATION_TEXT = "notificationTrigger";
    public static final String ERROR_SCHEDULING_NOTIFICATION_TEXT = "Помилка під час запланування нагадування.";
    public static final String ERROR_CANCELING_SCHEDULED_NOTIFICATION_TEXT = "Помилка під час відміни нагадування";
    public static final String NOTIFICATION_IN_LIST_TEMPLATE_TEXT = "%.12s \uD83D\uDD5A%s";
    public static final String NOTIFICATION_TEMPLATE_TEXT = """
            \uD83D\uDCE2 %s

            Нагадати знову через:""";
    public static final String BACK_TO_NOTIFICATION_LIST_TEXT = "Список нагадувань";
    public static final String BACK_TO_NOTIFICATION_MENU_TEXT = "Назад до меню";
    public static final String NOT_FOUND_DATE_TEXT = "Помилка при створені нагадування. Відсутній час або не правильно вказана, будь ласка спробуйте ще раз!";
    public static final String REPEAT_AFTER_15_MINUTES_TEXT = "15";
    public static final String REPEAT_AFTER_30_MINUTES_TEXT = "30";
    public static final String REPEAT_AFTER_45_MINUTES_TEXT = "45";
    public static final String REPEAT_AFTER_60_MINUTES_TEXT = "60";
    public static final String REPEAT_AFTER_1440_MINUTES_TEXT = "1440";
    public static final String REPEAT_AFTER_HOUR_TEXT = "1 hour";
    public static final String REPEAT_AFTER_DAY_TEXT = "1 day";
    public static final String NOTIFICATION_SUCCESSFULLY_DELAYED = "Нагадування успішно перенесено на ";
    public static final String NOTIFICATION_IS_NOT_AVAILABLE_TEXT = "Це нагадування вже видалено, будь ласка створіть нове";

    private TelegramTextResponses() {}
}
