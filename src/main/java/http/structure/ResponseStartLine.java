package http.structure;

public class ResponseStartLine {    // 간단히
    private final String startLine;

    private ResponseStartLine(String startLine) {
        this.startLine = "HTTP/1.1 " + startLine + " OK \r\n";
    }

    public static ResponseStartLine ofResponseCode(String responseCode) {
        return new ResponseStartLine(responseCode);
    }

    public byte[] toByte() {
        return startLine.getBytes();
    }
}
