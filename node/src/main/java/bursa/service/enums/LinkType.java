package bursa.service.enums;

public enum LinkType {
    GET_DOC("file/get-doc"),
    GET_VIDEO("file/get-video"),
    GET_AUDIO("file/get-audio"),
    GET_PHOTO("file/get-photo");

    private final String link;
    LinkType(final String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return link;
    }
}
