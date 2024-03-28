package structure;

public enum HeaderKey {
    COOKIE("Cookie"),
    CONTENT_LENGTH("Content-Length"),
    SET_COOKIE("Set-Cookie"),
    CONTENT_TYPE("Content-Type");

    private final String headerKey;

    HeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public String getHeaderKey() {
        return headerKey;
    }
}
