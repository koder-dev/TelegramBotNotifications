package bursa.controller;

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
        if (Objects.isNull(doc)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(MediaType.parseMediaType(doc.getMimeType()).toString());
        response.setHeader("Content-Disposition", "attachment; filename=" + doc.getDocName());
        response.setStatus(HttpServletResponse.SC_OK);
        var binaryContent = doc.getBinaryContent();

        try (var out = response.getOutputStream()){
            out.write(binaryContent.getFileAsArrayOfBytes());
        } catch (IOException e) {
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-video")
    public void getVideo(@RequestParam("id") String id, HttpServletResponse response) {
        var video = fileService.getVideo(id);
        if (Objects.isNull(video)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(MediaType.parseMediaType(video.getMimeType()).toString());
        response.setHeader("Content-Disposition", "attachment;");
        var binaryContent = video.getBinaryContent();
        response.setStatus(HttpServletResponse.SC_OK);
        try (var out = response.getOutputStream()){
            out.write(binaryContent.getFileAsArrayOfBytes());
        } catch (IOException e) {
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-photo")
    public void getPhoto(@RequestParam("id") String id, HttpServletResponse response) {
        var photo = fileService.getPhoto(id);
        if (Objects.isNull(photo)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(MediaType.IMAGE_JPEG.toString());
        response.setHeader("Content-Disposition", "attachment;");
        var binaryContent = photo.getBinaryContent();
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
        if (Objects.isNull(audio)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(MediaType.parseMediaType(audio.getMimeType()).toString());
        response.setHeader("Content-Disposition", "attachment;");
        var binaryContent = audio.getBinaryContent();
        response.setStatus(HttpServletResponse.SC_OK);
        try (var out = response.getOutputStream()){
            out.write(binaryContent.getFileAsArrayOfBytes());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
