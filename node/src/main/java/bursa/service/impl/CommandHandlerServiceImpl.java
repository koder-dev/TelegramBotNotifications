package bursa.service.impl;

import bursa.entities.*;
import bursa.enums.UserState;
import bursa.repositories.*;
import bursa.service.AppUserService;
import bursa.service.CommandHandlerService;
import bursa.service.enums.TelegramCommands;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

import static bursa.enums.UserState.BASIC_STATE;
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
        var message = SendMessage.builder();
        if (VIDEOS.equals(telegramCommand)) {
            message.text("Here your videos").replyMarkup(videoMarkup(user.getId(), 0));
        } else if (AUDIO.equals(telegramCommand)) {
            message.text("Ось ваші аудіо").replyMarkup(audioMarkup(user.getId(), 0));
        } else if (DOCS.equals(telegramCommand)) {
            message.text("Ось ваші документи").replyMarkup(docMarkup(user.getId(), 0));
        } else if (PHOTOS.equals(telegramCommand)) {
            message.text("Ось ваші фото").replyMarkup(photoMarkup(user.getId(), 0));
        } else if (BACK.equals(telegramCommand)) {
            user.setUserState(BASIC_STATE);
            appUserRepo.save(user);
            message.text("Вертаємося на головне меню").messageEffectId("5104841245755180586").replyMarkup(startMarkup());
        } else {
            message.text(UNKNOWN_COMMAND_TEXT);
        }
        return message.chatId(chatId).build();
    }

    private InlineKeyboardMarkup photoMarkup(Long userId, Integer pageNumber) {
        Pageable page = PageRequest.of(pageNumber, 10);
        List<AppPhoto> photos = appPhotoRepo.findByAppUserId(userId, page);
        var markupBuilder = InlineKeyboardMarkup.builder();
        photos.stream()
                .map(photo -> InlineKeyboardButton.builder()
                        .text(photo.getFileName())
                        .url(photo.getDownloadLink()).callbackData("Завантажити фото")
                        .build())
                .map(InlineKeyboardRow::new).forEach(markupBuilder::keyboardRow);
        return markupBuilder.build();
    }

    private InlineKeyboardMarkup docMarkup(Long userId, Integer pageNumber) {
        Pageable page = PageRequest.of(pageNumber, 10);
        List<AppDocument> docs = appDocumentRepo.findByAppUserId(userId, page);
        var markupBuilder = InlineKeyboardMarkup.builder();
        docs.stream()
                .map(doc -> InlineKeyboardButton.builder()
                        .text(doc.getDocName())
                        .url(doc.getDownloadLink()).callbackData("Завантажити документ")
                        .build())
                .map(InlineKeyboardRow::new).forEach(markupBuilder::keyboardRow);
        return markupBuilder.build();
    }

    private InlineKeyboardMarkup audioMarkup(Long userId, Integer pageNumber) {
        Pageable page = PageRequest.of(pageNumber, 10);
        List<AppAudio> audios = appAudioRepo.findByAppUserId(userId, page);
        var markupBuilder = InlineKeyboardMarkup.builder();
        audios.stream()
                .map(video -> InlineKeyboardButton.builder()
                        .text(video.getFileName())
                        .url(video.getDownloadLink()).callbackData("Завантажити аудіо")
                        .build())
                .map(InlineKeyboardRow::new).forEach(markupBuilder::keyboardRow);
        return markupBuilder.build();
    }

    private InlineKeyboardMarkup videoMarkup(Long userId, Integer pageNumber) {
        Pageable page = PageRequest.of(pageNumber, 10);
        List<AppVideo> videos = appVideoRepo.findByAppUserId(userId, page);
        var markupBuilder = InlineKeyboardMarkup.builder();
        videos.stream()
                .map(video -> InlineKeyboardButton.builder()
                        .text(video.getFileName())
                        .url(video.getDownloadLink()).callbackData("Завантажити відео")
                        .build())
                .map(InlineKeyboardRow::new).forEach(markupBuilder::keyboardRow);
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
