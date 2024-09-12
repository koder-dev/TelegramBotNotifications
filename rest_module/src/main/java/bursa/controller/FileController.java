package bursa.controller;

import bursa.entities.AppMedia;
import bursa.entities.AppPhoto;
import bursa.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
    public void getDoc(@RequestParam("id") String id, HttpServletResponse response) {
        var doc = fileService.getDocument(id);
        getMedia(response, doc);
    }

    @GetMapping("/get-video")
    public void getVideo(@RequestParam("id") String id, HttpServletResponse response) {
        var video = fileService.getVideo(id);
        getMedia(response, video);
    }

    @GetMapping("/get-photo")
    public void getPhoto(@RequestParam("id") String id, HttpServletResponse response) {
        AppPhoto photo = fileService.getPhoto(id);
        getMedia(response, photo);
    }

    private void getMedia(HttpServletResponse response, AppMedia media) {
        if (Objects.isNull(media)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(MediaType.parseMediaType(media.getMimeType()).toString());
        response.setHeader("Content-Disposition", "attachment;");
        var binaryContent = media.getBinaryContent();
        response.setStatus(HttpServletResponse.SC_OK);
        try (var out = response.getOutputStream()){
            out.write(binaryContent.getFileAsArrayOfBytes());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-audio")
    public void getAudio(@RequestParam("id") String id, HttpServletResponse response) {
        var audio = fileService.getAudio(id);
        getMedia(response, audio);
    }
}
