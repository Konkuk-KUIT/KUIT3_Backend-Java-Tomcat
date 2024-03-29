package webserver.httpRequest;

import domain.HttpMethod;
import domain.URL;

public class HttpStartLine {
    private String startLine;
    private String[] startLines;
    private HttpMethod method;
    private URL path;

    public HttpStartLine(String startLine) {
        String[] line = startLine.split(" ");
        this.method = HttpMethod.valueOf(line[0]);
        this.path = URL.valueOf(line[1]);
    }

    public HttpMethod getMethod(){
        return this.method;
    }

    public URL getPath() {
        return path;
    }


}
