package webserver;

public enum HttpHeader {
    HTTP200 ("HTTP/1.1 200 OK \r\n"),
    HTTP302 ("HTTP/1.1 302 Redirect \r\n"),
    ContextTypeHtml("Content-Type: text/html;charset=utf-8\r\n"),
    ContextTypeCss("Content-Type: text/css;charset=utf-8\r\n"),

    ContentLength("Content-Length: "),
    Location("Location: "),
    SetCookie("Set-Cookie: logined=true"),

    Escape("\r\n");

    final private String Header;



    public String getHeader(){
        return Header;
    }
    HttpHeader(String header) {
        Header = header;
    }
}
