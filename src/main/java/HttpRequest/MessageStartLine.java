package HttpRequest;

public class MessageStartLine {
    private String method;
    private String url;

    public MessageStartLine(String method, String url) {
        this.method = method;
        this.url = url;
    }

    public static MessageStartLine from(String startLine) {
        String[] startLines = startLine.split(" ");
        return new MessageStartLine(startLines[0],startLines[1]);
    }

    public void setStartLine(MessageStartLine startLine) {
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
