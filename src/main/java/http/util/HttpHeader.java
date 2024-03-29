package http.util;

public enum HttpHeader {
    HTTP_1_1("HTTP/1.1 "),
    LOCATION("Location: "),
    CONTENT_LENGTH("Content-Length: "),
    CONTENT_TYPE("Content-Type: "),
    SET_COOKIE("Set-Cookie: ");

    private final String header;

    HttpHeader(String header) {
        this.header = header;
    }
    public String getHeader() {
        return header;
    }
}
