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
import bursa.utils.DateTimeParser;
import bursa.utils.RenderUiTools;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import static bursa.enums.UserState.*;
import static bursa.service.enums.CallbackData.*;
import static bursa.strings.TelegramTextResponses.*;

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
            editNotificationMessage(update, dataArr[1], NOTIFICATION_EDIT_TEXT_STATE, NEW_DESCRIPTION_EDIT_MESSAGE_TEXT);
        } else if (EDIT_NOTIFICATION_TIME.equals(callbackData)) {
            editNotificationMessage(update, dataArr[1], NOTIFICATION_EDIT_TIME_STATE, NEW_TIME_EDIT_MESSAGE_TEXT);
        } else if (DELETE_NOTIFICATION.equals(callbackData)) {
            deleteNotification(update, dataArr[1]);
        } else if (REPEAT_NOTIFICATION_AFTER.equals(callbackData)) {
            repeatNotification(update, dataArr[1], dataArr[2]);
        } else {
            var message = SendMessage.builder().text(INTERNAL_SERVER_ERROR_TEXT).chatId(update.getCallbackQuery().getMessage().getChatId()).build();
            sendMessage(message);
        }
    }

    private void repeatNotification(Update update, String afterTime, String notificationId) {
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        deleteMessage(messageId, chatId);
        appNotificationsRepo.findById(Long.valueOf(notificationId)).ifPresentOrElse((notification) -> {
            var newNotifyTime = notification.getNotifyTime().plusMinutes(Long.parseLong(afterTime));
            notification.setNotifyTime(newNotifyTime);
            appNotificationsRepo.save(notification);
            SendMessage message = SendMessage.builder().text(NOTIFICATION_SUCCESSFULLY_DELAYED).chatId(chatId).build();
            sendMessage(message);
            quartSchedulerService.scheduleNotification(newNotifyTime, chatId, notification);
        }, () -> {
            var message = SendMessage.builder().text(NOTIFICATION_CANNOT_BE_DELAYED).chatId(chatId).build();
            sendMessage(message);
        });
    }

    private void createNotification(Update update, AppUser user) {
        var info = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        SendMessage message;
        try {
            var notifyTime = DateTimeParser.parse(info);
            var transientNotification = AppNotification.builder().appUser(user).notifyTime(notifyTime).notifyText(info).build();
            var persistedNotification = appNotificationsRepo.save(transientNotification);
            InlineKeyboardMarkup replyMarkup = RenderUiTools.renderBackMenu();
            message = SendMessage.builder().text(NOTIFICATION_SUCCESSFULLY_CREATED_TEXT).replyMarkup(replyMarkup).chatId(chatId).build();
            appUserRepo.save(user);
            quartSchedulerService.scheduleNotification(notifyTime, chatId, persistedNotification);
        } catch (NotCorrectDateFormat ex) {
            message = SendMessage.builder().text(ex.getMessage()).chatId(chatId).build();
        }
        sendMessage(message);
    }

    private void welcomeNotificationMessage(Long chatId) {
        var row = new InlineKeyboardRow();
        var createNewNotificationBtn = InlineKeyboardButton.builder().callbackData(CREATE_NEW_NOTIFICATION.toString()).text(CREATE_NEW_NOTIFICATION_BTN_TEXT).build();
        var showAllNotificationsBtn = InlineKeyboardButton.builder().callbackData(SHOW_ALL_NOTIFICATIONS.toString()).text(SHOW_ALL_NOTIFICATION_BTN_TEXT).build();

        row.add(createNewNotificationBtn);
        row.add(showAllNotificationsBtn);
        var inlineKeyboard = InlineKeyboardMarkup.builder().keyboardRow(row).build();
        var message = SendMessage.builder().text(WELCOME_MESSAGE_TEXT).replyMarkup(inlineKeyboard).chatId(chatId).build();
        sendMessage(message);
    }

    private void backToNotificationMenuMessage(Update update) {
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        deleteMessage(messageId, chatId);
        welcomeNotificationMessage(chatId);
    }

    @Override
    @Transactional
    public void processNotificationEditText(Update update) {
        var user = findOrSaveAppUser(update);
        var notification = appNotificationsRepo.findById(user.getEditingNotification()).orElseThrow();
        var text = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        notification.setNotifyText(text);
        appNotificationsRepo.save(notification);
        user.setUserState(NOTIFICATIONS_STATE);
        appUserRepo.save(user);
        InlineKeyboardMarkup replyMarkup = RenderUiTools.renderBackMenu();
        var message = SendMessage.builder().text(NOTIFICATION_DESCRIPTION_SUCCESSFULLY_CHANGED_TEXT).replyMarkup(replyMarkup).chatId(chatId).build();
        sendMessage(message);
    }


    @Override
    @Transactional
    public void processNotificationEditTime(Update update) {
        var user = findOrSaveAppUser(update);
        var notification = appNotificationsRepo.findById(user.getEditingNotification()).orElseThrow();
        var text = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        try {
            var startTime = DateTimeParser.parse(text);
            notification.setNotifyTime(startTime);
            appNotificationsRepo.save(notification);
            user.setUserState(NOTIFICATIONS_STATE);
            appUserRepo.save(user);
            quartSchedulerService.cancelNotification(user.getEditingNotification());
            quartSchedulerService.scheduleNotification(startTime, chatId, notification);
            InlineKeyboardMarkup replyMarkup = RenderUiTools.renderBackMenu();
            var message = SendMessage.builder().text(NOTIFICATION_TIME_SUCCESSFULLY_CHANGED_TEXT).replyMarkup(replyMarkup).chatId(chatId).build();
            sendMessage(message);
        } catch (NotCorrectDateFormat ex) {
            sendMessage(SendMessage.builder().text(ex.getMessage()).chatId(chatId).build());
        }
    }

    public void deleteNotification(Update update, String id) {
        var notificationId = Long.valueOf(id);
        appNotificationsRepo.deleteById(notificationId);
        var user = findOrSaveAppUser(update);
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        deleteMessage(messageId, chatId);
        InlineKeyboardMarkup replyMarkup = RenderUiTools.renderBackMenu();
        var message = SendMessage.builder().text(NOTIFICATION_SUCCESSFULLY_DELETED_TEXT).chatId(chatId).replyMarkup(replyMarkup).build();
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
        var keyboard = RenderUiTools.renderEditNotificationMenu(id);
        var message = SendMessage.builder().text(CHOOSE_EDIT_TEXT).chatId(chatId).replyMarkup(keyboard).build();
        sendMessage(message);
    }

    private void showAllNotificationMessage(Update update) {
        var user = findOrSaveAppUser(update);
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var notificationList = appNotificationsRepo.findByAppUserId(user.getId());
        deleteMessage(messageId, chatId);
        InlineKeyboardMarkup notificationKeyboardMarkup = RenderUiTools.renderNotificationList(notificationList);
        var message = SendMessage.builder()
                .text(notificationList.isEmpty() ? NO_NOTIFICATION_LIST_MESSAGE_TEXT : NOTIFICATION_LIST_MESSAGE_TEXT)
                .replyMarkup(notificationKeyboardMarkup)
                .chatId(chatId)
                .build();
        sendMessage(message);
    }

    private void deleteMessage(Integer messageId, Long chatId) {
        var messageToDelete = DeleteMessage.builder().messageId(messageId).chatId(chatId).build();
        notificationsProducerServiceImpl.produceDelete(messageToDelete);
    }

    private void createNewNotificationMessage(Update update) {
        EditMessageText editMessageText = EditMessageText.builder()
                .text(CREATE_NEW_NOTIFICATION_MESSAGE_TEXT)
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
        else throw new RuntimeException(INTERNAL_SERVER_ERROR_TEXT);
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
