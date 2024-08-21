package bursa.service.enums;

public enum LinkType {
    GET_DOC("file/get-doc"),
    GET_VIDEO("file/get-video");

    private final String link;
    private LinkType(final String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return link;
    }
}
