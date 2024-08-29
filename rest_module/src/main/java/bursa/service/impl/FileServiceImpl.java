package bursa.service.impl;

import bursa.entities.AppDocument;
import bursa.entities.AppVideo;
import bursa.repositories.AppDocumentRepo;
import bursa.repositories.AppVideoRepo;
import bursa.service.FileService;
import bursa.utils.CryptoTool;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

@Service
@Log4j
public class FileServiceImpl implements FileService {
    private final AppDocumentRepo appDocumentRepo;
    private final AppVideoRepo appVideoRepo;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(AppDocumentRepo appDocumentRepo, AppVideoRepo appVideoRepo, CryptoTool cryptoTool) {
        this.appDocumentRepo = appDocumentRepo;
        this.appVideoRepo = appVideoRepo;
        this.cryptoTool = cryptoTool;
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
