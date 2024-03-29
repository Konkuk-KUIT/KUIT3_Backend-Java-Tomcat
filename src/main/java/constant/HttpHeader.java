package constant;

public enum HttpHeader {

    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length");

    private final String type;

    HttpHeader(String type) {
        this.type = type;
    }

    public String getHeader(Object value) {
        return type + ": " + value + "\r\n";
    }

    public String getType() {
        return type;
    }

}
