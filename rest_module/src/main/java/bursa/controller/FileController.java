package bursa.controller;

import bursa.service.FileService;
import lombok.extern.log4j.Log4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/file")
@Log4j
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/get-doc")
    public ResponseEntity<?> getDoc(@RequestParam("id") String id) {
        var doc = fileService.getDocument(id);
        if (Objects.isNull(doc)) {
            return ResponseEntity.notFound().build();
        }
        var binaryContent = doc.getBinaryContent();
        var fileSystemResource = fileService.getFileSystemResource(binaryContent);
        if (Objects.isNull(fileSystemResource)) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getMimeType()))
                .header("Content-Disposition", "attachment; filename=" + doc.getDocName())
                .body(fileSystemResource);
    }

    @GetMapping("/get-video")
    public ResponseEntity<?> getVideo(@RequestParam("id") String id) {
        var video = fileService.getVideo(id);
        if (Objects.isNull(video)) {
            return ResponseEntity.badRequest().build();
        }
        var binaryContent = video.getBinaryContent();
        var fileSystemResource = fileService.getFileSystemResource(binaryContent);
        if (Objects.isNull(fileSystemResource)) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(video.getMimeType()))
                .header("Content-Disposition", "attachment;")
                .body(fileSystemResource);
    }
}
