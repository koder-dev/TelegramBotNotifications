package bursa.entities;

public interface AppMedia {
    String getDownloadLink();
    String getFileName();
    BinaryContent getBinaryContent();
    String getMimeType();
    Long getId();
    void setMimeType(String mimeType);
    void setTelegramFileId(String fileId);
    void setFileName(String fileName);
    void setBinaryContent(BinaryContent binaryContent);
    void setDownloadLink(String downloadLink);
    void setAppUser(AppUser appUser);

}
