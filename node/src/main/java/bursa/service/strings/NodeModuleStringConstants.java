package bursa.service.strings;

public class NodeModuleStringConstants {
    public static final String USER_ALREADY_REGISTERED_TEXT = "Ви вже зареєстровані!";
    public static final String MAIL_ALREADY_SENT_TEXT = "Ми вже надіслали вам лист для активації, будь ласка перевірте пошту.";
    public static final String WRITE_YOUR_MAIL_TEXT = "Введіть будь ласка ваш емейл.";
    public static final String INCORRECT_EMAIL_TEXT = "Введене некоректний емейл, будь ласка попробуйте інший.";
    public static final String SENDING_LETTER_ERROR_TEXT = "Під час надіслання листа на емейл: %s сталася помилка";
    public static final String LETTER_SENT_TEXT = "Ми надіслали листа вам на емейл. Будь ласка перевірте пошту.";
    public static final String EMAIL_IS_ALREADY_REGISTERED_TEXT = "Цей емейл вже зареєстрований, будь ласка введіть інший.";
    public static final String CHOOSE_COMMAND_TEXT = "Будь ласка виберіть одну з команд";
    public static final String UNKNOWN_COMMAND_TEXT = "Невідома команда, будь ласка введіть /cancel для відміни";
    public static final String CANCEL_COMMAND_TEXT = "Відміна команди";
    public static final String UNKNOWN_USER_STATE_ERROR_TEXT = "Внутрішня помилка серверу.Будь ласка введіть /cancel для відміни";
    public static final String DISC_COMMAND_TEXT = "Будь ласка, виберіть, що вас цікавить.";
    public static final String HELP_COMMAND_TEXT = """
            Ось список моїх основних можливостей:
            
            Ви можете створювати нагадування, просто надіславши мені повідомлення типу: "зателефонувати в офіс через 20 хвилин", "взяти документи завтра о 10", "перевірити готовність пирога🎂 через 10 хвилин" тощо.
           
            Коли я надсилаю вам нагадування, я додаю зручний набір кнопок, за допомогою яких ви можете налаштувати повторне нагадування в інший час. Якщо потрібно, ви також можете ввести цей час вручну.
           
            Ви можете видалити або змінити будь-яке нагадування.Достатньо вибрати його серед списку всіх нагадувань, після цього у вас будуть кнопки для редагування.
          
            Ви можете перетворити минулі нагадування в нотатки або запланувати їх на інший час. Також ви можете конвертувати нотатки в нагадування.
            
            Список доступниї команд:
                /start - Запускає бота та показує клавіатуру керування ботом.
                /registration - Реєстрація в боті для змоги надіслати файли та подальшого їх зберігання.
                /notifications - Меню нагадувань, основне місце з якого ви можете керувати вашими нагадуваннями.
                /cancel - Відміна останьої команди.Вертає вас в головне меню і скидую весь попередній стан, корисне при виникненю помилок.
            """;
}
