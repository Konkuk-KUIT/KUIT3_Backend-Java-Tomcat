package constant;

public enum StatusCode {

    Status200("HTTP/1.1 200 OK \r\n"), Status302("HTTP/1.1 302 Found \r\n");

    private final String header;

    StatusCode(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

}
