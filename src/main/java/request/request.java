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
    private HashMap<String, String> startLine;
    private String Path;
    private String Method;
    private Map<String, String> Query;
    private Map<String, String> requestHeader;
    private String body;
    BufferedReader br;

    private request(BufferedReader BR) {
        this.br = BR;
        String requestLine=null;
        try {
            requestLine = this.br.readLine();
            this.requestHeader=getHeader(this.br);
            String contentLength=requestHeader.get("Content-Length");
            body = IOUtils.readData(br,Integer.valueOf(contentLength));
        } catch (IOException e) {
            System.out.println("eror");
            log.log(Level.SEVERE, e.getMessage());
        }
        parseLine(requestLine);


    }

    public static request from(BufferedReader bufferedReader) {
        return new request(bufferedReader);
    }

    private static Map<String,String> getHeader(BufferedReader br/*, Map<String,String> reqHeader*/){
        String line=null;
        String[] pair;
        Map<String,String> reqHeader= new HashMap<String,String>();
        try {
            while((line = br.readLine()) != null && !line.isEmpty() ){
                //System.out.println("["+line+"]");
                pair = line.split(":");
                reqHeader.put(pair[0].trim(),pair[1].trim());
                //System.out.println("get header ["+pair[0]+":"+pair[1]+"]");
            }
        }catch(Exception e){
            System.out.println("getHeader error : "+ e);
            log.log(Level.SEVERE,e.getMessage());
        }
        System.out.println("get header end");
        return reqHeader;
    }
    private void parseLine(String requestLine){

        String method = null;
        String path=null;
        Map<String, String> query=null;
        if(requestLine!=null){
            System.out.println("request Line : "+ requestLine);
            String[] requestParts = requestLine.split(" ");
            //System.out.println("requestParts : "+ requestParts[0]+"  |  "+requestParts[1]+"  |  "+requestParts[2]);
            method = requestParts[0];
            if(requestParts[1].indexOf('?')==-1){
                System.out.println("\\? is can find indexOf()");
            }else{
                System.out.println("\\? is contain indexOf()");
            }
            System.out.println(Arrays.toString(requestParts));
            if(!requestParts[1].contains("?")){
                System.out.println("requestParts[1].contains(/?) is not true");
            }else{
                System.out.println("requestParts[1].contains(//?) is true");
            }

            if(requestParts[1].indexOf('?')!=-1) {
                String[] fullPath = requestParts[1].split("\\?");
                System.out.println("fullPath : " + fullPath[0]);

                path = fullPath[0];

                query = HttpRequestUtils.parseQueryParameter(fullPath[1]);
                System.out.println("method : " + method);
                System.out.println("fullPath : " + fullPath);
                System.out.println("path : " + path);
                System.out.println("query : " + query);
            }else{
                path = requestParts[1];
                System.out.println("path : " + path);
            }
        }
        this.Method = method;
        this.Path = path;
        this.Query = query;

    }

    public void printRequest(){
        System.out.println("req Path:"+this.Path);
        System.out.println("req method:"+this.Method);
        System.out.println("req Header----");
        requestHeader.forEach((key, value) -> {
            System.out.println(key + ":" + value);
        });
        System.out.println("req Header----end");
        System.out.println("req query:"+this.Query);
        System.out.println("req body:"+this.body);

    }

}
