package bursa.service.impl;

import bursa.entities.*;
import bursa.enums.UserState;
import bursa.repositories.*;
import bursa.service.AppUserService;
import bursa.service.CommandHandlerService;
import bursa.service.enums.CallbackData;
import bursa.service.enums.TelegramCommands;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static bursa.enums.UserState.BASIC_STATE;
import static bursa.service.enums.CallbackData.NEXT;
import static bursa.service.enums.CallbackData.PREV;
import static bursa.service.enums.TelegramCommands.*;
import static bursa.service.strings.NodeModuleStringConstants.*;

@Service
public class CommandHandlerServiceImpl implements CommandHandlerService {
    private final AppUserService appUserService;
    private final AppVideoRepo appVideoRepo;
    private final AppUserRepo appUserRepo;
    private final AppAudioRepo appAudioRepo;
    private final AppPhotoRepo appPhotoRepo;
    private final AppDocumentRepo appDocumentRepo;

    public CommandHandlerServiceImpl(AppUserService appUserService, AppVideoRepo appVideoRepo, AppUserRepo appUserRepo, AppAudioRepo appAudioRepo, AppPhotoRepo appPhotoRepo, AppDocumentRepo appDocumentRepo) {
        this.appUserService = appUserService;
        this.appVideoRepo = appVideoRepo;
        this.appUserRepo = appUserRepo;
        this.appAudioRepo = appAudioRepo;
        this.appPhotoRepo = appPhotoRepo;
        this.appDocumentRepo = appDocumentRepo;
    }

    @Override
    public SendMessage processCommand(AppUser appUser, TelegramCommands command, Long chatId) {
        var message = SendMessage.builder();
        if (REGISTRATION.equals(command)) {
            message.text(appUserService.registerUser(appUser));
        } else if (START.equals(command)) {
            message.text(CHOOSE_COMMAND_TEXT);
            message.replyMarkup(startMarkup());
        } else if (HELP.equals(command)) {
            message.text(HELP_COMMAND_TEXT);
        } else if (DISC.equals(command)) {
            appUser.setUserState(UserState.DISC_STATE);
            appUserRepo.save(appUser);
            message.text(DISC_COMMAND_TEXT).replyMarkup(discMenuMarkup());
        } else {
            message.text(UNKNOWN_COMMAND_TEXT);
        }
        return message.chatId(chatId).build();
    }

    @Override
    public EditMessageReplyMarkup processCallbackQuery(AppUser user, Update update) {
        var callbackDataString = update.getCallbackQuery().getData();
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var callbackDataArr = callbackDataString.split("/");
        int pageNumber = Integer.parseInt(callbackDataArr[2]);
        String mediaType = callbackDataArr[1];
        CallbackData callbackData = CallbackData.fromValue(callbackDataArr[0]);
        InlineKeyboardMarkup markup;
        if (NEXT.equals(callbackData)) {
            markup = mediaMarkup(user.getId(), pageNumber + 1, mediaType);
        } else if (PREV.equals(callbackData)) {
            markup = mediaMarkup(user.getId(), pageNumber - 1, mediaType);
        } else {
            markup = mediaMarkup(user.getId(), pageNumber, mediaType);
        }
        return EditMessageReplyMarkup.builder().replyMarkup(markup).chatId(chatId).messageId(messageId).build();
    }

    public ReplyKeyboardMarkup discMenuMarkup() {
        KeyboardRow row = new KeyboardRow();
        row.add("/videos");
        row.add("/photos");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("/audio");
        row2.add("/docs");
        KeyboardRow row3 = new KeyboardRow();
        row3.add("/cancel");
        row3.add("/back");
        return ReplyKeyboardMarkup.builder().keyboardRow(row).keyboardRow(row2).keyboardRow(row3).build();
    }

    @Override
    public SendMessage processDiscCommand(AppUser user, TelegramCommands telegramCommand, Long chatId) {
        var message = SendMessage.builder().text(CHOOSE_MEDIA_TEXT);
        if (VIDEOS.equals(telegramCommand)) {
            message.replyMarkup(mediaMarkup(user.getId(),0, DOWNLOAD_VIDEO_TEXT));
        } else if (AUDIO.equals(telegramCommand)) {
            message.replyMarkup(mediaMarkup(user.getId(),0, DOWNLOAD_AUDIO_TEXT));
        } else if (DOCS.equals(telegramCommand)) {
            message.replyMarkup(mediaMarkup(user.getId(),0, DOWNLOAD_DOC_TEXT));
        } else if (PHOTOS.equals(telegramCommand)) {
            message.replyMarkup(mediaMarkup(user.getId(),0, DOWNLOAD_PHOTO_TEXT));
        } else if (BACK.equals(telegramCommand)) {
            user.setUserState(BASIC_STATE);
            appUserRepo.save(user);
            message.messageEffectId("5104841245755180586").replyMarkup(startMarkup());
        } else {
            message.text(UNKNOWN_COMMAND_TEXT);
        }
        return message.chatId(chatId).build();
    }

    @Override
    public SendMessage cancelProcess(AppUser user, Long chatId) {
        user.setUserState(BASIC_STATE);
        appUserRepo.save(user);
        var markup = startMarkup();
        return SendMessage.builder().text(CANCEL_COMMAND_TEXT).replyMarkup(markup).chatId(chatId).build();
    }

    private InlineKeyboardMarkup mediaMarkup(Long userId, Integer pageNumber, String mediaType) {
        Pageable page = PageRequest.of(pageNumber, 10);
        Long mediaCount = 0L;
        List<? extends AppMedia> mediaList = switch (mediaType) {
            case DOWNLOAD_AUDIO_TEXT -> {
                mediaCount = appAudioRepo.countByAppUserId(userId);
                yield appAudioRepo.findByAppUserId(userId, page);
            }
            case DOWNLOAD_VIDEO_TEXT -> {
                mediaCount = appVideoRepo.countByAppUserId(userId);
                yield appVideoRepo.findByAppUserId(userId, page);
            }
            case DOWNLOAD_DOC_TEXT -> {
                mediaCount = appDocumentRepo.countByAppUserId(userId);
                yield appDocumentRepo.findByAppUserId(userId, page);
            }
            case DOWNLOAD_PHOTO_TEXT -> {
                mediaCount = appPhotoRepo.countByAppUserId(userId);
                yield appPhotoRepo.findByAppUserId(userId, page);
            }
            default -> new ArrayList<>();
        };
        var markupBuilder = InlineKeyboardMarkup.builder();
        mediaList.stream()
                .map(media -> InlineKeyboardButton.builder()
                        .text(media.getFileName())
                        .url(media.getDownloadLink())
                        .callbackData(mediaType)
                        .build())
                .map(InlineKeyboardRow::new).forEach(markupBuilder::keyboardRow);
        if (mediaCount > 10) {
            var navigationRow = new InlineKeyboardRow();
            if (mediaCount > (pageNumber + 1) * 10L) {
                navigationRow.add(InlineKeyboardButton.builder().text(NEXT_SYMBOL).callbackData(NEXT + "/" + mediaType + "/" + pageNumber).build());
            }
            if (pageNumber > 0) {
                navigationRow.add(InlineKeyboardButton.builder().text(PREV_SYMBOL).callbackData(PREV + "/" + mediaType + "/" + pageNumber).build());
            }
            markupBuilder.keyboardRow(navigationRow);
        }
        return markupBuilder.build();
    }


    private ReplyKeyboardMarkup startMarkup() {
        KeyboardRow row = new KeyboardRow();
        row.add("/start");
        row.add("/registration");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("/notifications");
        row2.add("/disc");
        KeyboardRow row3 = new KeyboardRow();
        row3.add("/cancel");
        return ReplyKeyboardMarkup.builder().keyboardRow(row).keyboardRow(row2).keyboardRow(row3).build();
    }
}
