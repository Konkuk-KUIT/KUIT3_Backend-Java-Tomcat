package HttpResponse;

public enum ResponseHeaderConstants {
    START_LINE_200("HTTP/1.1 200 OK \r\n"),
    START_LINE_302("HTTP/1.1 302 Found \r\n"),
    CONTENT_TYPE_HTML("Content-Type: text/html;charset=utf-8\r\n"),
    CONTENT_TYPE_CSS("Content-Type: text/css;charset=utf-8\r\n");

    private final String value;

    ResponseHeaderConstants(String value) {
        this.value = value;
    }

    public String getValue() { return this.value; }
}
