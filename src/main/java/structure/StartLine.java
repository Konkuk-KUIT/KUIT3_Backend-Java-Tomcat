package structure;

import http.HttpRequest;
import http.util.HttpRequestUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StartLine {
    private final HttpMethod httpMethod;
    private final String path;

    private final Map<String, String> queryString;
    private final String version;

    public StartLine(String line) {
        String[] startLines = line.split(" ");
        this.httpMethod = HttpMethod.valueOf(String.valueOf(startLines[0]));
        this.path = getPath(startLines[1]);
        this.queryString = getQueryString(startLines[1]);
        this.version = startLines[2];
    }

    private String getPath(String line) {   // TODO: line 진짜 맘에 안들어, 아래두
        return line.split("\\?")[0];
    }

    private Map<String, String> getQueryString(String line) {
        String queryString = line.split("\\?")[1];

        return parseQueryParameter(queryString);
    }

    private Map<String, String> parseQueryParameter (String queryString) {
        return HttpRequestUtils.parseQueryParameter(queryString);
    }

    @Override
    public String toString() {  // toString() is only for debugging
        return "StartLine{" +
                "httpMethod=" + httpMethod +
                ", path='" + path + '\'' +
                ", queryString=" + queryString +
                ", version='" + version + '\'' +
                '}';
    }

    public String getPath() {
        return this.path;
    }

    public boolean isPost() {
        return this.httpMethod == HttpMethod.POST;
    }

    public boolean isGet() {
        return this.httpMethod == HttpMethod.GET;
    }

    public boolean isMatchingPath(String...paths) {     // TODO: 얘가 처리하는 게 맞을까..? 각자 Controller에서?
        for(String path : paths) {
            if(this.path.equals(path)) {
                return true;
            }
        }
        return false;
    }
}
