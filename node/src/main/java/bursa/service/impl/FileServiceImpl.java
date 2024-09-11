package bursa.service.impl;

import bursa.entities.*;
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
    public AppVideo processVideo(Message telegramMessage, AppUser user) {
        Video telegramVideo = telegramMessage.getVideo();
        String fileId = telegramVideo.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persitentBinaryContent = getPersistentBinaryContent(response);
            AppVideo transientAppVideo = buildTransientAppVideo(telegramVideo, persitentBinaryContent, user);
            return appVideoRepo.save(transientAppVideo);
        } else {
            throw new UploadFileException("Bad response from telegram service in video downloading: " + response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message message, AppUser user) {
        PhotoSize photoSize = message.getPhoto().get(message.getPhoto().size() - 1);
        String fileId = photoSize.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persitentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppPhoto = buildTransientAppPhoto(photoSize, persitentBinaryContent, user);
            return appPhotoRepo.save(transientAppPhoto);
        } else {
            throw new UploadFileException("Bad response from telegram service in video downloading: " + response);
        }
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize photoSize, BinaryContent persitentBinaryContent, AppUser user) {
        return AppPhoto.builder()
                .fileName("Photo " + photoSize.getFileId())
                .telegramFileId(photoSize.getFileId())
                .binaryContent(persitentBinaryContent)
                .appUser(user)
                .build();
    }

    @Override
    public AppAudio processAudio(Message message, AppUser user) {
        Audio audio = message.getAudio();
        String fileId = audio.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persitentBinaryContent = getPersistentBinaryContent(response);
            AppAudio transientAppAudio = buildTransientAppAudio(audio, persitentBinaryContent, user);
            return appAudioRepo.save(transientAppAudio);
        } else {
            throw new UploadFileException("Bad response from telegram service in video downloading: " + response);
        }
    }

    private AppAudio buildTransientAppAudio(Audio audio, BinaryContent persitentBinaryContent, AppUser user) {
        return AppAudio.builder()
                .telegramFileId(audio.getFileId())
                .fileName(audio.getFileName())
                .appUser(user)
                .fileSize(audio.getFileSize())
                .mimeType(audio.getMimeType())
                .duration(audio.getDuration())
                .binaryContent(persitentBinaryContent)
                .build();
    }

    private AppVideo buildTransientAppVideo(Video telegramVideo, BinaryContent persitentBinaryContent, AppUser user) {
        return AppVideo.builder()
                .appUser(user)
                .telegramFileId(telegramVideo.getFileId())
                .fileName(telegramVideo.getFileName())
                .fileSize(telegramVideo.getFileSize())
                .mimeType(telegramVideo.getMimeType())
                .duration(telegramVideo.getDuration())
                .binaryContent(persitentBinaryContent)
                .build();
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            Document telegramDoc = telegramMessage.getDocument();
            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
            return appDocumentRepo.save(transientAppDoc);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    private @NotNull BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder().fileAsArrayOfBytes(fileInByte).build();
        return binaryContentRepo.save(transientBinaryContent);
    }

    private static String getFilePath(ResponseEntity<String> response) {
        JSONObject json = new JSONObject(response.getBody());
        return String.valueOf(json.getJSONObject("result").getString("file_path"));
    }

    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{bot.token}", botToken)
                .replace("{filePath}", filePath);
        URL urlObj;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException("Creating URL failed: " + e);
        }

        try (InputStream inputStream = urlObj.openStream()){
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException("Downloading file failed: " + e);
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
