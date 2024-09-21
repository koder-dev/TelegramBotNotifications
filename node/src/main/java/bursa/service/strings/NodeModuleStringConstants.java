package bursa.service.strings;

public class NodeModuleStringConstants {
    public static final String HELP_COMMAND_TEXT = "/help";
    public static final String REGISTRATION_COMMAND_TEXT = "/registration";
    public static final String NOTIFICATION_COMMAND_TEXT = "/notification";
    public static final String SETTINGS_COMMAND_TEXT = "/settings";
    public static final String CANCEL_COMMAND_TEXT = "/cancel";
    public static final String START_COMMAND_TEXT = "/start";
    public static final String BACK_COMMAND_TEXT = "/back";
    public static final String DISC_COMMAND_TEXT = "/disc";
    public static final String VIDEOS_COMMAND_TEXT = "/videos";
    public static final String AUDIO_COMMAND_TEXT = "/audio";
    public static final String DOCS_COMMAND_TEXT = "/docs";
    public static final String PHOTOS_COMMAND_TEXT = "/photos";
    public static final String USER_ALREADY_REGISTERED_TEXT = "Ви вже зареєстровані!";
    public static final String MAIL_ALREADY_SENT_TEXT = "Ми вже надіслали вам лист для активації, будь ласка перевірте пошту.";
    public static final String WRITE_YOUR_MAIL_TEXT = "✉️ Введіть будь ласка ваш емейл✉.";
    public static final String INCORRECT_EMAIL_TEXT = "Введене некоректний емейл , будь ласка попробуйте інший.";
    public static final String LETTER_SENT_TEXT = "✉️ Ми надіслали листа вам на емейл. Будь ласка перевірте пошту.";
    public static final String EMAIL_IS_ALREADY_REGISTERED_TEXT = "Цей емейл вже зареєстрований, будь ласка введіть інший.";
    public static final String CHOOSE_COMMAND_TEXT = "Будь ласка виберіть одну з команд";
    public static final String UNKNOWN_COMMAND_TEXT = "Невідома команда, будь ласка введіть /cancel для відміни";
    public static final String CANCEL_MESSAGE_TEXT = "Відміна команди";
    public static final String UNKNOWN_USER_STATE_ERROR_TEXT = "Внутрішня помилка серверу.Будь ласка введіть /cancel для відміни";
    public static final String DISC_MESSAGE_TEXT = "Будь ласка, виберіть, що вас цікавить.";
    public static final String UPLOAD_FILE_EXCEPTION_TEXT = "Під час завантаження файлу сталася помилка. Будь ласка введіть /cancel та спробуйте ще раз.";
    public static final String DOWNLOADING_FILE_FAILED_TEXT = "Сталася помилка під час завантаження файлу з ресурсу.Будь ласка введіть /cancel та спробуйте пізніше!";
    public static final String CREATING_URL_FAILED_TEXT = "Внутрішня помилка серверу під час створення url!Будь ласка введіть /cancel та спробуйте пізніше!";
    public static final String DOWNLOAD_DOC_TEXT = "Завантажити документ";
    public static final String DOWNLOAD_PHOTO_TEXT = "Завантажити фотографію";
    public static final String DOWNLOAD_VIDEO_TEXT = "Завантажити відео";
    public static final String DOWNLOAD_AUDIO_TEXT = "Завантажити аудіозапис";
    public static final String IS_REGISTERED_TRUE_TEXT = "користувач зареєстрований";
    public static final String IS_REGISTERED_FALSE_TEXT = "користувач не зареєстрований";
    public static final String NEXT_SYMBOL = "➡️";
    public static final String PREV_SYMBOL = "⬅️";
    public static final String BACK_RESPONSE_TEXT = "Вертаємося до головного меню.";
    public static final String CHOOSE_MEDIA_TEXT = "Виберіть медіа яке ви хочете завантажити";
    public static final String INCORRECT_MEDIA_CLASS_TEXT = "Не підтримувальний тип файлу. Будь ласка введіть /cancel для відміни.";
    public static final String MEDIAL_HAS_UPLOADED_TEXT = "✅ Медіа успішно завантажене на сервер.Ви можете його заванатижити за цим посиланням - ";
    public static final String NOT_REGISTERED_ACCOUNT_TEXT = "Зареєструйть, щоб мати змогу завантажувати медіа.Для реєстрації введіть /registration";
    public static final String SETTINGS_MESSAGE_TEMPLATE_TEXT = "Ваші настройки:\n\n-часовий пояс:%s\n-локальний час:%s\n-стан реєстрації: %s";
    public static final String HELP_MESSAGE_TEXT = """
            HelperBot — це Телеграм-бот, створений для зручного керування сповіщеннями та збереження файлів у своїй базі даних. Просто надішліть файл, і він буде збережений для подальшого завантаження.
            
            🔑 Основні можливості:
            Реєстрація користувачів: Реєстрація через електронну пошту для отримання доступу до функцій бота.
            Гнучке планування сповіщень: Ви можете планувати нагадування з різними варіантами введення дати.
            Збереження файлів: Легко надсилайте та зберігайте документи, фото, відео та інші файли.
            Підтримка різних форматів дат: Введіть час у форматах 18:00, завтра о 20:15, 30.09 17:59.
            Сумісність з файлами: Підтримка документів, фото, аудіо та відео.
            
            📝 Як працює HelperBot:
            🔔 Створення нагадувань: Просто надішліть повідомлення типу "зателефонувати в офіс через 20 хвилин" або "взяти документи завтра о 10" — і бот нагадає вам вчасно!
            
            🔄 Зручні кнопки: Після нагадування можна налаштувати повтор або змінити час прямо через інтерактивні кнопки.
            
            🗑️ Редагування та видалення: Ви можете легко змінити або видалити нагадування зі списку.
            
            📌 Конвертація нотаток: Перетворюйте нагадування в нотатки або плануйте їх на інший час.
            
            📜 Список команд:
            /start — Запуск бота та показ меню.
            /registration — Реєстрація для збереження файлів та нагадувань.
            /notifications — Меню керування вашими нагадуваннями.
            /disc - Меню керування збереженними файлами.
            /cancel — Скидання останньої команди та повернення до головного меню.
            """;


    private NodeModuleStringConstants() {}
}
