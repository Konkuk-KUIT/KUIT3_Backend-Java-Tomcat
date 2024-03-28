package HttpRequest;

public class RequestStartLine {
    private String method;
    private String url;

    public RequestStartLine(String method, String url) {
        this.method = method;
        this.url = url;
    }

    public static RequestStartLine from(String startLine) {
        String[] startLines = startLine.split(" ");
        return new RequestStartLine(startLines[0],startLines[1]);
    }

    public void setStartLine(RequestStartLine startLine) {
        this.method = startLine.method;
        this.url = startLine.url;
    }

    public String getMethod() {
        return this.method;
    }

    public String getUrl() {
        return this.url;
    }

}
