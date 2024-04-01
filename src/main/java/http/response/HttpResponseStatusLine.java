package http.response;

public class HttpResponseStatusLine {
    private String version;
    private String statusCode;
    private String statusMessage;

    public HttpResponseStatusLine(String version, String statusCode, String statusMessage) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getVersion() {
        return version;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
