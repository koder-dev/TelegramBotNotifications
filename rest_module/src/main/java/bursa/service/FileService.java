package bursa.service;

import bursa.entities.*;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    AppDocument getDocument(String id);
    AppVideo getVideo(String id);
    AppPhoto getPhoto(String id);
    AppAudio getAudio(String id);
}
