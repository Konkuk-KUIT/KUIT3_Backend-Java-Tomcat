package request;

import http.util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class requestStartLine {
    private static final Logger log = Logger.getLogger(requestStartLine.class.getName());
    private String path;
    private String method;
    private String HTTPversion;
    private Map<String,String> query;
    private String[] fullPath;

    private requestStartLine(String requestLine){
        String Method = null;
        String Path=null;
        Map<String, String> Query=null;
        if(requestLine!=null){
            System.out.println("request Line : "+ requestLine);
            String[] requestParts = requestLine.split(" ");
            //System.out.println("requestParts : "+ requestParts[0]+"  |  "+requestParts[1]+"  |  "+requestParts[2]);
            this.method = requestParts[0];
            this.HTTPversion = requestParts[2];

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
                this.fullPath = requestParts[1].split("\\?");
                System.out.println("fullPath : " + this.fullPath[0]);

                this.path = this.fullPath[0];

                this.query = HttpRequestUtils.parseQueryParameter(this.fullPath[1]);
                System.out.println("method : " + this.method);
                System.out.println("fullPath : " + this.fullPath);
                System.out.println("path : " + this.path);
                System.out.println("query : " + this.query);
            }else{
                path = requestParts[1];
                System.out.println("path : " + this.path);
            }
        }

    }

    public static requestStartLine from(BufferedReader br){
        try {
            return new requestStartLine(br.readLine());
        }catch(IOException e ){
            log.log(Level.SEVERE,e.getMessage());
        }
        return null;
    }

    public String getMethod(){
        return method;
    }
    public String getPath(){
        return path;
    }
    public Map<String,String> getAllQuery(){
        return query;
    }
    public String getQuery(String key){
        return query.get(key);
    }
    public String getHTTPversion(){
        return HTTPversion;
    }
    public String getFullPath(){
        return fullPath[1];
    }
    public void printAll(){
        System.out.println("request");
        System.out.println("Method"+method);
        System.out.println("Path"+fullPath);
        System.out.println(HTTPversion);
    }
}
