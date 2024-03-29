package http.util;

public enum HttpStatusCode2XX implements HttpStatusCode {
    OK("200 OK");


    HttpStatusCode2XX(String statusCode) {
        this.statusCode = statusCode;
    }

    private String statusCode;

    @Override
    public String getValue() {
        return this.statusCode;
    }
}
