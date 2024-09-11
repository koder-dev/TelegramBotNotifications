package bursa.string;

public class MailTextConst {
    public static final String MAIL_SUBJECT_TEXT = "Активація аккаунту в HelperBot";
    public static final String MAIL_SENT_OK_RESPONSE_TEXT = "Лист надісланий до вашого емейлу. Будь ласка перевірте його";
    public static final String MAIL_BODY_TEXT = """
            Доброго дня, дорогий користувачу нашого бота!
            
            Дякуємо за реєстрацію в HelperBot. Для завершення процесу реєстрації та активації вашого облікового запису, будь ласка, перейдіть за посиланням нижче:
            
            %s
            
            Якщо ви не реєструвалися в HelperBot, проігноруйте цей лист.
            
            З найкращими побажаннями, \s
            Команда HelperBot
            """;
}
