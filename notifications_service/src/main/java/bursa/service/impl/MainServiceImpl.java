package bursa.service.impl;

import bursa.entities.AppNotification;
import bursa.entities.AppUser;
import bursa.enums.UserState;
import bursa.repositories.AppNotificationsRepo;
import bursa.repositories.AppUserRepo;
import bursa.service.MainService;
import bursa.service.QuartSchedulerService;
import bursa.service.enums.CallbackData;
import bursa.service.exceptions.NotCorrectDateFormat;
import bursa.utils.DateParser;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.time.LocalDateTime;
import java.util.List;

import static bursa.enums.UserState.*;
import static bursa.service.enums.CallbackData.*;

@Service
public class MainServiceImpl implements MainService {
    private final NotificationsProducerServiceImpl notificationsProducerServiceImpl;
    private final AppUserRepo appUserRepo;
    private final AppNotificationsRepo appNotificationsRepo;
    private final QuartSchedulerService quartSchedulerService;

    public MainServiceImpl(AppUserRepo appUserRepo, AppNotificationsRepo appNotificationsRepo, NotificationsProducerServiceImpl notificationsProducerServiceImpl, QuartSchedulerService quartSchedulerService) {
        this.appUserRepo = appUserRepo;
        this.appNotificationsRepo = appNotificationsRepo;
        this.notificationsProducerServiceImpl = notificationsProducerServiceImpl;
        this.quartSchedulerService = quartSchedulerService;
    }

    @Override
    public void processNotificationMessage(Update update) {
        var user = findOrSaveAppUser(update);
        var userState = user.getUserState();
        if (BASIC_STATE.equals(userState)) {
            welcomeNotificationMessage(update.getMessage().getChatId());
            user.setUserState(NOTIFICATIONS_STATE);
            appUserRepo.save(user);
        } else if (NOTIFICATIONS_STATE.equals(userState)) {
            createNotification(update, user);
        }
    }

    private void createNotification(Update update, AppUser user) {
        var info = update.getMessage().getText().split("/");
        var chatId = update.getMessage().getChatId();
        var notifyTime = DateParser.parse(info[1]);
        var notifyText = info[0];
        if (notifyTime.isBefore(LocalDateTime.now())) throw new NotCorrectDateFormat("You can only make notification for future", chatId);
        var transientNotification = AppNotification.builder().appUser(user).notifyTime(notifyTime).notifyText(notifyText).build();
        var persistedNotification = appNotificationsRepo.save(transientNotification);
        var messageText = String.format("Notification with id: %s successfully created!", persistedNotification.getId());
        var message = SendMessage.builder().text(messageText).replyMarkup(renderBackMenu()).chatId(chatId).build();
        user.setUserState(BASIC_STATE);
        appUserRepo.save(user);
        sendMessage(message);
        quartSchedulerService.scheduleNotification(notifyTime, chatId, persistedNotification);
    }

    private void welcomeNotificationMessage(Long chatId) {
        var row = new InlineKeyboardRow();
        var createNewNotificationBtn = InlineKeyboardButton.builder().callbackData(CREATE_NEW_NOTIFICATION.toString()).text("Create New Notification").build();
        var showAllNotificationsBtn = InlineKeyboardButton.builder().callbackData(SHOW_ALL_NOTIFICATIONS.toString()).text("Show All Notifications").build();

        row.add(createNewNotificationBtn);
        row.add(showAllNotificationsBtn);
        var inlineKeyboard = InlineKeyboardMarkup.builder().keyboardRow(row).build();
        var message = SendMessage.builder().text("Please choose what you want to do").replyMarkup(inlineKeyboard).chatId(chatId).build();
        sendMessage(message);
    }

    @Override
    public void processNotificationCallbackQuery(Update update) {
        var dataArr = update.getCallbackQuery().getData().split("/");
        CallbackData callbackData = CallbackData.fromValue(dataArr[0]);
        if (CREATE_NEW_NOTIFICATION.equals(callbackData)) {
            createNewNotificationMessage(update);
        } else if (BACK_TO_NOTIFICATION_MENU.equals(callbackData)) {
            backToNotificationMenuMessage(update);
        } else if (SHOW_ALL_NOTIFICATIONS.equals(callbackData)) {
            showAllNotificationMessage(update);
        } else if (EDIT_NOTIFICATION.equals(callbackData)) {
            editNotificationMessageMenu(update, dataArr[1]);
        } else if (EDIT_NOTIFICATION_TEXT.equals(callbackData)) {
            var text = "Please enter a new description for notification";
            editNotificationMessage(update, dataArr[1], NOTIFICATION_EDIT_TEXT_STATE, text);
        } else if (EDIT_NOTIFICATION_TIME.equals(callbackData)) {
            var text = "Please enter a new time for notification in format yyyyy-mm-ddTHH:mm:ss";
            editNotificationMessage(update, dataArr[1], NOTIFICATION_EDIT_TIME_STATE, text);
        } else if (DELETE_NOTIFICATION.equals(callbackData)) {
            deleteNotification(update, dataArr[1]);
        } else {
            var error = "Unsupported callbackDataType!Please enter /cancel to back to the menu";
            var message = SendMessage.builder().text(error).chatId(update.getCallbackQuery().getMessage().getChatId()).build();
            sendMessage(message);
        }
    }

    private void backToNotificationMenuMessage(Update update) {
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        deleteMessage(messageId, chatId);
        welcomeNotificationMessage(chatId);
    }

    @Override
    public void processNotificationEditText(Update update) {
        var user = findOrSaveAppUser(update);
        var notification = appNotificationsRepo.findById(user.getEditingNotification()).orElseThrow();
        var text = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        notification.setNotifyText(text);
        appNotificationsRepo.save(notification);
        user.setUserState(NOTIFICATIONS_STATE);
        appUserRepo.save(user);
        var message = SendMessage.builder().text("Notification description successfully changed").replyMarkup(renderBackMenu()).chatId(chatId).build();
        sendMessage(message);
    }


    @Override
    public void processNotificationEditTime(Update update) {
        var user = findOrSaveAppUser(update);
        var notification = appNotificationsRepo.findById(user.getEditingNotification()).orElseThrow();
        var text = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        var startTime = DateParser.parse(text);
        if (startTime.isBefore(LocalDateTime.now())) throw new NotCorrectDateFormat("You can only make notification for future", chatId);
        notification.setNotifyTime(startTime);
        appNotificationsRepo.save(notification);
        user.setUserState(NOTIFICATIONS_STATE);
        appUserRepo.save(user);
        quartSchedulerService.cancelNotification(user.getEditingNotification());
        quartSchedulerService.scheduleNotification(startTime, chatId, notification);
        var message = SendMessage.builder().text("Notification time successfully changed").replyMarkup(renderBackMenu()).chatId(chatId).build();
        sendMessage(message);
    }

    private void deleteNotification(Update update, String id) {
        var notificationId = Long.valueOf(id);
        appNotificationsRepo.deleteById(notificationId);
        var user = findOrSaveAppUser(update);
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        deleteMessage(messageId, chatId);
        var message = SendMessage.builder().text("Notification successfully deleted").chatId(chatId).replyMarkup(renderBackMenu()).build();
        sendMessage(message);
        user.setUserState(NOTIFICATIONS_STATE);
        appUserRepo.save(user);
    }

    private void editNotificationMessage(Update update, String id, UserState userState, String text) {
        var user = findOrSaveAppUser(update);
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        deleteMessage(messageId, chatId);
        user.setEditingNotification(Long.valueOf(id));
        user.setUserState(userState);
        appUserRepo.save(user);
        var message = SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .build();
        sendMessage(message);
    }

    private void editNotificationMessageMenu(Update update, String id) {
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        deleteMessage(messageId, chatId);
        var text = "Please choose what you want to do";
        var keyboard = renderEditNotificationMenu(id);
        var message = SendMessage.builder().text(text).chatId(chatId).replyMarkup(keyboard).build();
        sendMessage(message);
    }

    private InlineKeyboardMarkup renderBackMenu() {
        var row = new InlineKeyboardRow();
        var backToMainMenuBtn = InlineKeyboardButton.builder()
                .text("Back to main menu")
                .callbackData(BACK_TO_MAIN_MENU.toString())
                .build();
        var backToNotificationMenuBtn = InlineKeyboardButton.builder()
                .text("Back to notification menu")
                .callbackData(BACK_TO_NOTIFICATION_MENU.toString())
                .build();
        row.add(backToMainMenuBtn);
        row.add(backToNotificationMenuBtn);
        return InlineKeyboardMarkup.builder().keyboardRow(row).build();
    }

    private InlineKeyboardMarkup renderEditNotificationMenu(String id) {
        var row = new InlineKeyboardRow();
        var EditTimeBtn = InlineKeyboardButton.builder()
                .text("Change to another time")
                .callbackData(EDIT_NOTIFICATION_TIME + "/" + id)
                .build();
        var EditTextBtn = InlineKeyboardButton.builder()
                .text("Change text")
                .callbackData(EDIT_NOTIFICATION_TEXT + "/" + id)
                .build();
        var DeleteBtn = InlineKeyboardButton.builder()
                .text("Delete notification")
                .callbackData(DELETE_NOTIFICATION + "/" + id)
                .build();
        row.add(EditTimeBtn);
        row.add(EditTextBtn);
        row.add(DeleteBtn);
        return InlineKeyboardMarkup.builder().keyboardRow(row).build();
    }

    private void showAllNotificationMessage(Update update) {
        var user = findOrSaveAppUser(update);
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var notificationList = appNotificationsRepo.findByAppUserId(user.getId());
        deleteMessage(messageId, chatId);
        InlineKeyboardMarkup notificationKeyboardMarkup = renderNotificationList(notificationList);
        var message = SendMessage.builder().text("Here your notifications!").replyMarkup(notificationKeyboardMarkup).chatId(chatId).build();
        sendMessage(message);
    }

    private InlineKeyboardMarkup renderNotificationList(List<AppNotification> notificationList) {
        var inlineKeyboard = InlineKeyboardMarkup.builder();
        notificationList.stream().map((notification) -> {
            InlineKeyboardRow row = new InlineKeyboardRow();
            var btnText = notification.getNotifyText() + " " + notification.getNotifyTime().toString();
            var btn = InlineKeyboardButton.builder().text(btnText).callbackData(EDIT_NOTIFICATION + "/" +notification.getId().toString()).build();
            row.add(btn);
            return row;
        }).forEach(inlineKeyboard::keyboardRow);
        return inlineKeyboard.build();
    }

    private void deleteMessage(Integer messageId, Long chatId) {
        var messageToDelete = DeleteMessage.builder().messageId(messageId).chatId(chatId).build();
        notificationsProducerServiceImpl.produceDelete(messageToDelete);
    }

    private void createNewNotificationMessage(Update update) {
        EditMessageText editMessageText = EditMessageText.builder()
                .text("Write a notification info in format.\nNotifyText/TIME\nExample:Dinner/18:00 or Meeting/30.09 18:00\nDate formats - 18:00, 30.09 17:59, 30 September 17:32, 2024-09-02T23:20:53")
                .chatId(update.getCallbackQuery().getMessage().getChatId())
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .build();
        sendEdit(editMessageText);
    }

    private void sendEdit(EditMessageText editMessageText) {
        notificationsProducerServiceImpl.produceEdit(editMessageText);
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser;
        if (update.hasCallbackQuery()) telegramUser = update.getCallbackQuery().getFrom();
        else if (update.hasMessage()) telegramUser = update.getMessage().getFrom();
        else throw new RuntimeException("Incorrect update message");
        return appUserRepo.findByTelegramUserId(telegramUser.getId()).orElseGet(() -> {
            AppUser appUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .userState(BASIC_STATE)
                    .build();
            return appUserRepo.save(appUser);
        });
    }


    public void sendMessage(SendMessage message) {
        notificationsProducerServiceImpl.produceAnswer(message);
    }
}
