package response;

import request.request;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class response {
    private static final Logger log = Logger.getLogger(response.class.getName());
    private DataOutputStream dos;
    private String StartLine;
    private Map<String,String>  Header=new HashMap<String,String>();

    private StringBuilder HeaderBuilder =new StringBuilder();
    private byte[] body;
    //String
    private String HTTPVersion = "HTTP/1.1";

    private response(DataOutputStream DOS){
        dos = DOS;
    }
    public static response from(DataOutputStream DOS){
        return new response(DOS);
    }


    public void redirect(String URL){
        System.out.println("redirect URL : "+URL);
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            //dos.writeBytes("location: "+ URL +"\r\n");
            setHeader("location: "+ URL+"\r\n");
            responseHeader();
        } catch (IOException e) {
            System.out.println("response 302  error");
            log.log(Level.SEVERE, e.getMessage());
        }

        /*try {
        } catch (IOException e) {
            System.out.println("response 302 error");
            log.log(Level.SEVERE, e.getMessage());
        }*/


    }

    public void forward(String filePath){
        System.out.println("forward start");
        try{
            if(filePath.contains("webapp")){
                this.body = Files.readAllBytes(new File("."+filePath).toPath());
            }else{
                this.body = Files.readAllBytes(new File("./webapp" + filePath).toPath());

            }
            /*if(filePath==null){
                System.out.println(Paths.get("webapp"+filePath).toString());
            }
            System.out.println(Paths.get(("webapp"+filePath)));*/
            //System.out.println(new String(body));
        }catch(IOException e){
            System.out.println("put body IO error");
            log.log(Level.SEVERE, e.getMessage());
        }catch (Exception e){
            System.out.println("put body IO error:"+e);
            log.log(Level.SEVERE, e.getMessage());
        }
        try{
            if(filePath.endsWith(".js")){
                //Header.put("Content-Type", "application/javascript");
                setHeader("Content-Type", "application/javascript");
            }else if(filePath.endsWith(".css")){
                //Header.put("Content-Type", "text/css");
                setHeader("Content-Type", "text/css");
            }else{
                //Header.put("Content-Type", "text/css");
                setHeader("Content-Type", "text/html;charset=utf-8");
            }
            if(body.length==0){
                System.out.println("body is empty");
            }
            Header.put("Content-Length",String.valueOf(this.body.length));
        }catch (Exception e){
            System.out.println("set file .  IO error:"+e);
            log.log(Level.SEVERE, e.getMessage());
        }

        this.printResponse();
        try{
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            responseHeader();
            dos.writeBytes("\r\n");
            responseBody(dos,this.body);
        }catch (IOException e ){
            System.out.println("response write error");
            log.log(Level.SEVERE, e.getMessage());
        }


    }


    public void printResponse(){
        System.out.println(HeaderBuilder.toString());
        //System.out.println(new String(body));

    }
    private void set200response(){
        this.StartLine = HTTPVersion + " " + "200"+ " " + "OK"+ " " +"\r\n";
    }
    private void set302response(){
        this.StartLine = HTTPVersion + " " + "302"+ " " + "FOUND"+ " " +"\r\n";
    }
    public void setHeader(String HeaderPair){
        this.HeaderBuilder.append(HeaderPair);
        if(!HeaderPair.contains("\n")){
            this.HeaderBuilder.append("\r\n");
        }
    }
    public void setHeader(Map<String,String> HeaderPair){
        if(HeaderPair!=null&&!HeaderPair.isEmpty()) {
            HeaderPair.forEach((Key, Value) -> {
                setHeader(Key + ": " + Value);
            });
        }
    }
    public void setHeader(String key,String value){
        if(key!=null&&!value.isEmpty()) {
                setHeader(key + ": " + value);
        }
    }

    public void responseHeader(){
        try {
            this.dos.writeBytes(HeaderBuilder.toString());
        }catch(IOException e){
            System.out.println("responseHeader error");
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            System.out.println("eror1");
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response200CSS(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            System.out.println("eror1");
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response200HeaderAdd(DataOutputStream dos, int lengthOfBodyContent,String headAdder) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes(headAdder);
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            System.out.println("eror1");
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response302Header(DataOutputStream dos, String URL,String headerAdder) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("location: "+ URL +"\r\n");
            //dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            if(headerAdder.length()!=0){
                dos.writeBytes(headerAdder);
            }
            dos.writeBytes("\r\n");
            //dos.flush();
        } catch (IOException e) {
            System.out.println("response 302 error");
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            System.out.println("responseBody eror2");
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
