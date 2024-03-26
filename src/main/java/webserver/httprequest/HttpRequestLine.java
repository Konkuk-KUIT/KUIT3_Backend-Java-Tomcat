package webserver.httprequest;

public class HttpRequestLine {
    private HttpMethod method;
    private String path;

    public HttpRequestLine(String startLine) {
        String[] line = startLine.split(" ");
        this.method = HttpMethod.valueOf(line[0]);
        this.path = line[1];
    }

    public String getMethod() {
        return method.getMethod();
    }

    public String getPath() {
        return path;
    }
}
