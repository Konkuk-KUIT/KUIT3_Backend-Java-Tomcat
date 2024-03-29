package webserver.http;

public class HttpHeader {

    private int requestContentLength;
    private String cookie="";


    public HttpHeader(final String line) {

        while (true) {
            if (line.equals("")) {
                break;
            }
            if (line.startsWith("Content-Length")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }

            if (line.startsWith("Cookie")) {
                cookie = line.split(": ")[1];
            }
        }
    }

    public int getRequestContentLength(){
        return requestContentLength;
    }
    public String getCookie(){
        return cookie;
    }
}
