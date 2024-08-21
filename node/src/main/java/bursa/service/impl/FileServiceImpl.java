package bursa.service.impl;

import bursa.entities.AppDocument;
import bursa.entities.AppVideo;
import bursa.entities.BinaryContent;
import bursa.exceptions.UploadFileException;
import bursa.repositories.AppDocumentRepo;
import bursa.repositories.AppVideoRepo;
import bursa.repositories.BinaryContentRepo;
import bursa.service.FileService;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class FileServiceImpl implements FileService {
    @Value("${bot.token}")
    private String botToken;

    @Value("${service.file.info.uri}")
    private String fileInfoUri;

    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    private AppDocumentRepo appDocumentRepo;
    private AppVideoRepo appVideoRepo;
    private BinaryContentRepo binaryContentRepo;

    public FileServiceImpl(AppDocumentRepo appDocumentRepo, AppVideoRepo appVideoRepo, BinaryContentRepo binaryContentRepo) {
        this.appDocumentRepo = appDocumentRepo;
        this.appVideoRepo = appVideoRepo;
        this.binaryContentRepo = binaryContentRepo;
    }

    @Override
    public AppVideo processVideo(Message telegramMessage) {
        Video telegramVideo = telegramMessage.getVideo();
        String fileId = telegramVideo.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persitentBinaryContent = getPersistentBinaryContent(response);
            AppVideo transientAppVideo = buildTransientAppVideo(telegramVideo, persitentBinaryContent);
            return appVideoRepo.save(transientAppVideo);
        } else {
            throw new UploadFileException("Bad response from telegram service in video downloading: " + response);
        }
    }

    private AppVideo buildTransientAppVideo(Video telegramVideo, BinaryContent persitentBinaryContent) {
        return AppVideo.builder()
                .telegramFileId(telegramVideo.getFileId())
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
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException("Creating URL failed: " + e);
        }

        //TODO подумати над оптимізацією

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
}
