package bursa.service.impl;

import bursa.entities.*;
import bursa.exceptions.IncorrectMediaClassException;
import bursa.exceptions.UploadFileException;
import bursa.repositories.*;
import bursa.service.FileService;
import bursa.service.enums.LinkType;
import bursa.utils.CryptoTool;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static bursa.service.strings.NodeModuleStringConstants.*;

@Service
public class FileServiceImpl implements FileService {
    private final AppPhotoRepo appPhotoRepo;
    private final AppAudioRepo appAudioRepo;
    @Value("${bot.token}")
    private String botToken;

    @Value("${service.file.info.uri}")
    private String fileInfoUri;

    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    @Value("${link.address}")
    private String linkAddress;
    private final CryptoTool cryptoTool;
    private final AppDocumentRepo appDocumentRepo;
    private final AppVideoRepo appVideoRepo;
    private final BinaryContentRepo binaryContentRepo;

    public FileServiceImpl(CryptoTool cryptoTool, AppDocumentRepo appDocumentRepo, AppVideoRepo appVideoRepo, BinaryContentRepo binaryContentRepo, AppPhotoRepo appPhotoRepo, AppAudioRepo appAudioRepo) {
        this.cryptoTool = cryptoTool;
        this.appDocumentRepo = appDocumentRepo;
        this.appVideoRepo = appVideoRepo;
        this.binaryContentRepo = binaryContentRepo;
        this.appPhotoRepo = appPhotoRepo;
        this.appAudioRepo = appAudioRepo;
    }

    @Override
    public AppVideo processVideo(Message telegramMessage, AppUser user) throws UploadFileException, IncorrectMediaClassException {
        Video telegramVideo = telegramMessage.getVideo();
        String fileId = telegramVideo.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persitentBinaryContent = getPersistentBinaryContent(response);
            AppVideo transientAppVideo = (AppVideo) buildMedia(telegramVideo.getFileId(), telegramVideo.getFileName(), telegramVideo.getMimeType(), persitentBinaryContent, user, AppVideo.class);
            return appVideoRepo.save(transientAppVideo);
        } else {
            throw new UploadFileException(UPLOAD_FILE_EXCEPTION_TEXT);
        }
    }

    @Override
    public AppPhoto processPhoto(Message message, AppUser user) throws UploadFileException, IncorrectMediaClassException {
        PhotoSize photoSize = message.getPhoto().get(message.getPhoto().size() - 1);
        String fileId = photoSize.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persitentBinaryContent = getPersistentBinaryContent(response);
            String photoName = photoSize.getFileId().substring(0, 4) + ".jpg";
            AppPhoto transientAppPhoto = (AppPhoto) buildMedia(photoSize.getFileId(), photoName , MediaType.IMAGE_JPEG.toString(), persitentBinaryContent, user, AppPhoto.class);
            return appPhotoRepo.save(transientAppPhoto);
        } else {
            throw new UploadFileException(UPLOAD_FILE_EXCEPTION_TEXT);
        }
    }

    @Override
    public AppAudio processAudio(Message message, AppUser user) throws UploadFileException, IncorrectMediaClassException {
        Audio audio = message.getAudio();
        String fileId = audio.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persitentBinaryContent = getPersistentBinaryContent(response);
            AppAudio transientAppAudio = (AppAudio) buildMedia(audio.getFileId(), audio.getFileName(), audio.getMimeType(), persitentBinaryContent, user, AppDocument.class);
            return appAudioRepo.save(transientAppAudio);
        } else {
            throw new UploadFileException(UPLOAD_FILE_EXCEPTION_TEXT + response);
        }
    }

    @Override
    public AppDocument processDoc(Message telegramMessage, AppUser appUser) throws UploadFileException, IncorrectMediaClassException {
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            Document telegramDoc = telegramMessage.getDocument();
            AppDocument transientAppDoc = (AppDocument) buildMedia(telegramDoc.getFileId(), telegramDoc.getFileName(), telegramDoc.getMimeType(), persistentBinaryContent, appUser, AppDocument.class);
            return appDocumentRepo.save(transientAppDoc);
        } else {
            throw new UploadFileException(UPLOAD_FILE_EXCEPTION_TEXT + response);
        }
    }

    private @NotNull BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) throws UploadFileException {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder().fileAsArrayOfBytes(fileInByte).build();
        return binaryContentRepo.save(transientBinaryContent);
    }

    private static String getFilePath(ResponseEntity<String> response) {
        JSONObject json = new JSONObject(response.getBody());
        return String.valueOf(json.getJSONObject("result").getString("file_path"));
    }


    private AppMedia buildMedia(String fileId, String fileName, String mimeType, BinaryContent persistentBinaryContent, AppUser user, Class<? extends AppMedia> cls) throws IncorrectMediaClassException {
        AppMedia media;
        if (cls.equals(AppDocument.class)) media = new AppDocument();
        else if (cls.equals(AppAudio.class)) media = new AppAudio();
        else if (cls.equals(AppVideo.class)) media = new AppVideo();
        else if (cls.equals(AppPhoto.class)) media = new AppPhoto();
        else throw new IncorrectMediaClassException(INCORRECT_MEDIA_CLASS_TEXT);
        media.setTelegramFileId(fileId);
        media.setAppUser(user);
        media.setBinaryContent(persistentBinaryContent);
        media.setFileName(fileName);
        media.setMimeType(mimeType);
        return media;
    }

    private byte[] downloadFile(String filePath) throws UploadFileException {
        String fullUri = fileStorageUri.replace("{bot.token}", botToken)
                .replace("{filePath}", filePath);
        URL urlObj;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(CREATING_URL_FAILED_TEXT, e);
        }

        try (InputStream inputStream = urlObj.openStream()){
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(DOWNLOADING_FILE_FAILED_TEXT, e);
        }
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        return restTemplate.exchange(fileInfoUri, HttpMethod.GET, request, String.class, botToken, fileId);
    }

    @Override
    public String generateLink(Long id, LinkType linkType) {
        var hash = cryptoTool.hashOf(id);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }
}
