package request;

import http.util.HttpRequestUtils;
import http.util.IOUtils;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class request {
    private static final Logger log = Logger.getLogger(request.class.getName());
    private requestStartLine startLine;
    private requestHeader header;
    private requestBody body;

    //private HashMap<String, String> startLine;
    //private String Path;
    //private String Method;
    //private Map<String, String> Query;
    ///private Map<String, String> requestHeader;
    //private String body;


    private request(BufferedReader BR) {
        this.startLine= requestStartLine.from(BR);
        this.header = requestHeader.from(BR);
        this.body = requestBody.from(BR,this.header);

    }

    public static request from(BufferedReader bufferedReader) {
        return new request(bufferedReader);
    }

    //HTTP first Line
    public String getPath(){
        return startLine.getPath();
    }
    public String getMethod(){
        return startLine.getMethod();
    }

    public String getParamsQuery(String paramKey){
        return startLine.getQuery(paramKey);
    }
    public Map<String,String> getParamsAllQuery(){
        return startLine.getAllQuery();
    }

    public String getAllPath(){
        return startLine.getFullPath();
    }

    public String getHTTPVersion(){
        return startLine.getHTTPversion();
    }

    //HTTP Headers
    public String getHeader(String Key){
        return header.getHeader(Key);
    }
    public Map<String,String> getAllHeader(){
        return header.getAllHeader();
    }

    //HTTP Body
    public String getBody(){
        return body.getBody();
    }
    public String getBodyQuery(String Key){
        return body.getBodyQuery(Key);
    }
    public Map<String,String> getAllBodyQuery(){
        return body.getAllBodyQuery();
    }

    public void printRequest(){
        startLine.printAll();
        header.printHeader();
        body.printBody();
        body.printBodyQuery();
    }

}
