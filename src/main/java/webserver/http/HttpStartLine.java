package webserver.http;

public class HttpStartLine {


    //서버로 부터 오는 Header 분석하는 부분 //////////
    private String[] startLines;
    //startLines : ["GET", "/", "HTTP/1.1"]
    private String method ;
    //method : "GET"
    private String url ;
    //url : "/"


    public HttpStartLine( String startLine) {
        this.startLines = startLine.split(" ");
        this.method = startLines[0];
        this.url = startLines[1];
    }

    public String getMethod(){
        return method;
    }
    public String getUrl(){
        return url;
    }
}
