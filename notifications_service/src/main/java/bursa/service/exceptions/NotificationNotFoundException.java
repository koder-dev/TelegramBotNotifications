package bursa.service.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationNotFoundException extends RuntimeException {
    private Long chatId;
    private Long notificationId;

    public NotificationNotFoundException(String message,Long chatId, Long notificationId) {
        super(message);
        this.chatId = chatId;
        this.notificationId = notificationId;
    }


    public NotificationNotFoundException(String message) {
        super(message);
    }

    public NotificationNotFoundException() {
    }
}
