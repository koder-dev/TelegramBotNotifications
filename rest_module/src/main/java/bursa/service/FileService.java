package bursa.service;

import bursa.entities.AppDocument;
import bursa.entities.AppVideo;
import bursa.entities.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    AppDocument getDocument(String id);
    AppVideo getVideo(String id);
}
