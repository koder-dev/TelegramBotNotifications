package bursa.service.exceptions;

import lombok.Getter;

@Getter
public class NotCorrectDateFormat extends RuntimeException {
    private Long chatId;

    public NotCorrectDateFormat() {
        super();
    }

    public NotCorrectDateFormat(String message, Long chatId) {
        super(message);
        this.chatId = chatId;
    }
}
