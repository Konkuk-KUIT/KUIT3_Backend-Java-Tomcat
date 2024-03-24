package structure;

import java.util.List;

public class StartLine {
    private final HttpMethod httpMethod;
    private final String path;
    private final String version;

    public StartLine(String startLine) {
        String[] startLines = startLine.split(" ");
        this.httpMethod = HttpMethod.valueOf(String.valueOf(startLines[0]));
        this.path = startLines[1];
        this.version = startLines[2];
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
