package bursa.utils;

import bursa.entities.AppNotification;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

import static bursa.service.enums.CallbackData.*;
import static bursa.service.enums.CallbackData.DELETE_NOTIFICATION;
import static bursa.strings.TelegramTextResponses.*;

public class RenderUiTools {

    public static InlineKeyboardMarkup renderBackMenu() {
        var row = new InlineKeyboardRow();
        var backToMainMenuBtn = InlineKeyboardButton.builder()
                .text(BACK_TO_NOTIFICATION_LIST_TEXT)
                .callbackData(SHOW_ALL_NOTIFICATIONS.toString())
                .build();
        var backToNotificationMenuBtn = InlineKeyboardButton.builder()
                .text(BACK_TO_NOTIFICATION_MENU_TEXT)
                .callbackData(BACK_TO_NOTIFICATION_MENU.toString())
                .build();
        row.add(backToMainMenuBtn);
        row.add(backToNotificationMenuBtn);
        return InlineKeyboardMarkup.builder().keyboardRow(row).build();
    }

    public static InlineKeyboardMarkup renderRepeatNotificationButtons(Long id) {
        var row = new InlineKeyboardRow();
        var row2 = new InlineKeyboardRow();
        var btn15 = InlineKeyboardButton.builder().text(REPEAT_AFTER_15_MINUTES_TEXT).callbackData(REPEAT_NOTIFICATION_AFTER + "/" + REPEAT_AFTER_15_MINUTES_TEXT + "/" + id).build();
        var btn30 = InlineKeyboardButton.builder().text(REPEAT_AFTER_30_MINUTES_TEXT).callbackData(REPEAT_NOTIFICATION_AFTER + "/" + REPEAT_AFTER_30_MINUTES_TEXT + "/" + id).build();
        var btn45 = InlineKeyboardButton.builder().text(REPEAT_AFTER_45_MINUTES_TEXT).callbackData(REPEAT_NOTIFICATION_AFTER + "/" + REPEAT_AFTER_45_MINUTES_TEXT + "/" + id).build();
        var btn1hour = InlineKeyboardButton.builder().text(REPEAT_AFTER_HOUR_TEXT).callbackData(REPEAT_NOTIFICATION_AFTER + "/" + REPEAT_AFTER_60_MINUTES_TEXT + "/" + id).build();
        var btn1day = InlineKeyboardButton.builder().text(REPEAT_AFTER_DAY_TEXT).callbackData(REPEAT_NOTIFICATION_AFTER + "/" + REPEAT_AFTER_1440_MINUTES_TEXT + "/" + id).build();
        var deleteBtn = InlineKeyboardButton.builder().text(DELETE_BTN_TEXT).callbackData(DELETE_NOTIFICATION + "/" + id).build();
        row.add(btn15);
        row.add(btn30);
        row.add(btn45);
        row.add(btn1hour);
        row.add(btn1day);
        row2.add(deleteBtn);
        return InlineKeyboardMarkup.builder().keyboardRow(row).keyboardRow(row2).build();
    }

    public static InlineKeyboardMarkup renderNotificationList(List<AppNotification> notificationList) {
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

    public static InlineKeyboardMarkup renderEditNotificationMenu(String id) {
        var row = new InlineKeyboardRow();
        var row2 = new InlineKeyboardRow();
        var editTimeBtn = InlineKeyboardButton.builder()
                .text(CHANGE_TIME_BTN_TEXT)
                .callbackData(EDIT_NOTIFICATION_TIME + "/" + id)
                .build();
        var editTextBtn = InlineKeyboardButton.builder()
                .text(CHANGE_TEXT_BTN_TEXT)
                .callbackData(EDIT_NOTIFICATION_TEXT + "/" + id)
                .build();
        var deleteBtn = InlineKeyboardButton.builder()
                .text(DELETE_BTN_TEXT)
                .callbackData(DELETE_NOTIFICATION + "/" + id)
                .build();
        var backToListBtn = InlineKeyboardButton.builder().text(BACK_TO_NOTIFICATION_LIST_TEXT).callbackData(SHOW_ALL_NOTIFICATIONS.toString()).build();
        row.add(editTimeBtn);
        row.add(editTextBtn);
        row.add(deleteBtn);
        row2.add(backToListBtn);
        return InlineKeyboardMarkup.builder().keyboardRow(row).keyboardRow(row2).build();
    }
}
