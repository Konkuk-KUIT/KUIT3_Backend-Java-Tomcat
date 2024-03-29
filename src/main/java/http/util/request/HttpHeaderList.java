package http.util.request;

public enum HttpHeaderList {
    HTTP_1_1("HTTP/1.1 "),
    LOCATION("Location: "),
    CONTENT_LENGHT("Content-Length: "),
    CONTENT_TYPE("Content-Type: "),
    SET_COOKIE("Set-Cookie: ");

    private String header;

    HttpHeaderList(String header){
        this.header = header;
    }

    @Override
    public String toString() {
        return header;
    }
}
