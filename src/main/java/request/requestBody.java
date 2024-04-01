package request;

import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
public class requestBody {
    private static final Logger log = Logger.getLogger(requestBody.class.getName());
    private String body;
    private Map<String ,String > bodyQuery;
    private requestHeader requestheader;

    private requestBody(BufferedReader br,String contentLength){
        //contentLength=requestHeader.get("Content-Length");
        //System.out.println("get fun null test:"+contentLength);
        if(contentLength!=null){
            //Integer number = Integer.valueOf(str);
            if(!contentLength.matches("[0-9.]+")){
                System.out.println("content length error : is not number : "+ contentLength);
                return ;
            }
            try {
                this.body = IOUtils.readData(br, Integer.valueOf(contentLength));
                System.out.println("body print : " + body);
            }catch(IOException e ){
                System.out.println("getBody error : "+ e);
                log.log(Level.SEVERE,e.getMessage());
            }
        }
        bodyQuery= null;

    }

    public static requestBody from(BufferedReader br,requestHeader reqHeader){
        //requestheader = reqHeader;
        return new requestBody(br,reqHeader.getHeader("Content-Length"));
    }
    public Map<String,String> getAllBodyQuery(){
        if(bodyQuery==null){
             parseBody(body);
        }
        return bodyQuery;
    }
    public String getBodyQuery(String Key){
        if(bodyQuery==null){
            parseBody(body);
        }
        if(!bodyQuery.containsKey(Key)){
            return null;
        }
        return bodyQuery.get(Key);
    }

    private void parseBody(String Body){
        bodyQuery=HttpRequestUtils.parseQueryParameter(Body);
    }
    public String getBody(){
        return body;
    }
    public void printBody(){
        System.out.println("body : "+this.body);
    }
    public void printBodyQuery(){
        if(bodyQuery==null){
            parseBody(body);
        }
        System.out.println("body Query :");
        bodyQuery.forEach((key, value) -> {
            System.out.println(key + ":" + value);
        });

    }
}
