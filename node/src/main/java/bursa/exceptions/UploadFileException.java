package bursa.exceptions;

public class UploadFileException extends RuntimeException {
    public UploadFileException() {}

    public UploadFileException(String message) {
        super(message);
    }
}
