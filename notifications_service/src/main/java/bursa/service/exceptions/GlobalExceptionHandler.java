package bursa.service.exceptions;

import bursa.service.NotificationsProducerService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@ControllerAdvice
public class GlobalExceptionHandler {
    private NotificationsProducerService notificationsProducerService;

    public GlobalExceptionHandler(NotificationsProducerService notificationsProducerService) {
        this.notificationsProducerService = notificationsProducerService;
    }

    @ExceptionHandler(NotCorrectDateFormat.class)
    public void handleNotCorrectDateFormat(NotCorrectDateFormat notCorrectDateFormat) {
        var chatId = notCorrectDateFormat.getChatId();
        var message = SendMessage.builder().chatId(chatId).text(notCorrectDateFormat.getMessage()).build();
        notificationsProducerService.produceAnswer(message);
    }
}
