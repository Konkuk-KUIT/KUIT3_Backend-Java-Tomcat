package webserver;

public enum HttpStatus {
    OK(200, "OK"),
    REDIRECT(302, "REDIRECT");

    private final int statusCode;
    private final String reasonPhrase;

    HttpStatus(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public String getStatusLine() {
        return "HTTP/1.1 " + this.statusCode + " " + this.reasonPhrase + " \r\n";
    }
}
