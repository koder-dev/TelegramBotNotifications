package bursa.service.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationNotFoundException extends RuntimeException {
    private Long chatId;
    private Long notificationId;


}
