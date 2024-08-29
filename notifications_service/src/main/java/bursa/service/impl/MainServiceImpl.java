package bursa.service.impl;

import bursa.service.MainService;
import bursa.service.ProducerService;
import bursa.service.enums.CallbackData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import static bursa.service.enums.CallbackData.CREATE_NEW_NOTIFICATION;
import static bursa.service.enums.CallbackData.SHOW_ALL_NOTIFICATIONS;

@Service
public class MainServiceImpl implements MainService {
    private final ProducerService producerService;

    public MainServiceImpl(ProducerService producerService) {
        this.producerService = producerService;
    }

    @Override
    public void processNotificationMessage(Update update) {
        var row = new InlineKeyboardRow();
        var createNewNotificationBtn = InlineKeyboardButton.builder().callbackData(CREATE_NEW_NOTIFICATION.toString()).text("Create New Notification").build();
        var showAllNotificationsBtn = InlineKeyboardButton.builder().callbackData(SHOW_ALL_NOTIFICATIONS.toString()).text("Show All Notifications").build();

        row.add(createNewNotificationBtn);
        row.add(showAllNotificationsBtn);
        var inlineKeyboard = InlineKeyboardMarkup.builder().keyboardRow(row).build();
        var chatId = update.getMessage().getChatId();
        var message = SendMessage.builder().text("Please choose what you want to do").replyMarkup(inlineKeyboard).chatId(chatId).build();
        sendMessage(message);
    }

    @Override
    public void processNotificationCallbackQuery(Update update) {
        CallbackData callbackData = CallbackData.valueOf(update.getCallbackQuery().getData());
        if (CREATE_NEW_NOTIFICATION.equals(callbackData)) {
            createNewNotification(update);
        }
    }

    private void createNewNotification(Update update) {
        EditMessageText editMessageText = EditMessageText.builder().text("Ok, lets create new").build();
        sendMessage(editMessageText);

    }


    public void sendMessage(SendMessage message) {
        producerService.produceAnswer(message);
    }
}
