package bursa.service.impl;

import bursa.entities.AppAudio;
import bursa.entities.AppDocument;
import bursa.entities.AppPhoto;
import bursa.entities.AppVideo;
import bursa.repositories.AppAudioRepo;
import bursa.repositories.AppDocumentRepo;
import bursa.repositories.AppPhotoRepo;
import bursa.repositories.AppVideoRepo;
import bursa.service.FileService;
import bursa.utils.CryptoTool;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {
    private final AppDocumentRepo appDocumentRepo;
    private final AppVideoRepo appVideoRepo;
    private final CryptoTool cryptoTool;
    private final AppPhotoRepo appPhotoRepo;
    private final AppAudioRepo appAudioRepo;

    public FileServiceImpl(AppDocumentRepo appDocumentRepo, AppVideoRepo appVideoRepo, CryptoTool cryptoTool, AppPhotoRepo appPhotoRepo, AppAudioRepo appAudioRepo) {
        this.appDocumentRepo = appDocumentRepo;
        this.appVideoRepo = appVideoRepo;
        this.cryptoTool = cryptoTool;
        this.appPhotoRepo = appPhotoRepo;
        this.appAudioRepo = appAudioRepo;
    }

    @Override
    public AppPhoto getPhoto(String hashId) {
        Long id = cryptoTool.idOf(hashId);
        return appPhotoRepo.findById(id).orElse(null);
    }

    @Override
    public AppAudio getAudio(String hashId) {
        Long id = cryptoTool.idOf(hashId);
        return appAudioRepo.findById(id).orElse(null);
    }

    @Override
    public AppDocument getDocument(String hashId) {
        Long id = cryptoTool.idOf(hashId);
        return appDocumentRepo.findById(id).orElse(null);
    }

    @Override
    public AppVideo getVideo(String hashId) {
        Long id = cryptoTool.idOf(hashId);
        return appVideoRepo.findById(id).orElse(null);
    }

}
