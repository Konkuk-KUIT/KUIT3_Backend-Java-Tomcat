package http.response;

public class HttpResponseStatusLine {
    private String version;
    private String statusCode;
    private String statusMessage;

    public HttpResponseStatusLine(String version, String statusMessage, String statusCode) {
        this.version = version;
        this.statusMessage = statusMessage;
        this.statusCode = statusCode;
    }

    public String getVersion() {
        return version;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
