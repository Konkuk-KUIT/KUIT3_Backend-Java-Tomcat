package webserver;
import model.User;
import request.request;
import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    private MemoryUserRepository DB= MemoryUserRepository.getInstance();
    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        String path=null;
        BufferedReader br=null;
        DataOutputStream dos=null;
        String requestLine=null;
        StringBuilder redirection = new StringBuilder("");
        StringBuilder headerAdder = new StringBuilder("");
        boolean cssre = false;


        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            br = new BufferedReader(new InputStreamReader(in));
            dos = new DataOutputStream(out);

            requestLine= br.readLine();
            Map<String, String> query=null;
            Map<String, String> requestHeader=null;
            //File Index = new File("index.html");
            //byte[] body = "Hello World".getBytes();

            //request work

        /*try{

        }catch(IOException e){
            System.out.println("read line eror");
            log.log(Level.SEVERE,e.getMessage());
        }*/
            String method = null;
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

            requestHeader = getHeader(br);
            System.out.println("req Header----");
            requestHeader.forEach((key, value) -> {
                System.out.println(key + ":" + value);
            });
            System.out.println("req Header----end");

            String contentLength ;
            String body=null;
            contentLength=requestHeader.get("Content-Length");
            System.out.println("get fun null test:"+contentLength);
            if(contentLength!=null){
                //Integer number = Integer.valueOf(str);
                if(!contentLength.matches("[0-9.]+")){
                    System.out.println("content length error : is not number : "+ contentLength);
                }

                body = IOUtils.readData(br,Integer.valueOf(contentLength));
                System.out.println("body print : "+body);
            }



            //response
            byte[] responesBody=null;

            //System.out.println(Paths.get("./index.html").toAbsolutePath());
            responesBody = getBody(path,query,method,body,requestHeader,redirection,headerAdder,cssre);


            //check User repository
            System.out.println("--------User---------\n");
            for(User user:DB.findAll()){
                System.out.println("["+user.getUserId()+","+user.getName()+","+user.getPassword()+","+user.getEmail());
            }
            System.out.println("--------User end---------\n");

            System.out.println("file leng : "+ responesBody.length);
            System.out.println("\n\nredirection:"+redirection);


            if(responesBody==null){
                System.out.println("wNULL");
            }else{
                System.out.println(responesBody);
            }
            System.out.println("work");
            if(path.endsWith(".css") ){
                System.out.println("CSS response");
                response200CSS(dos, responesBody.length);
                responseBody(dos, responesBody);
            }
            if((redirection.length()!=0)){
                System.out.println("redirection:"+redirection);
                response302Header(dos,redirection.toString(),headerAdder.toString());
            }else if(headerAdder.length()!=0) {
                System.out.println("HeadAdder start: "+headerAdder);
                response200HeaderAdd(dos, responesBody.length,headerAdder.toString());
                responseBody(dos, responesBody);
            }else{
                response200Header(dos, responesBody.length);
                responseBody(dos, responesBody);
            }



        } catch (IOException e) {
            System.out.println("eror");
            log.log(Level.SEVERE,e.getMessage());
        }

    }

    private byte[] getBody(String path,Map<String, String> query,String method , String body,Map<String, String> requestHeader,StringBuilder redirection,StringBuilder headAdder,boolean css){
        if(path.endsWith(".css") ){
            css=true;
            try {
                //return Files.readAllBytes(Paths.get("D:/Codes/KUIT mission/week_2_tomcat/webapp/index.html"));
                return Files.readAllBytes(Paths.get("webapp/css/styles.css"));
            }catch(Exception e){
                System.out.println("winit ork");
                System.out.println("error name "+ e);
                log.log(Level.SEVERE,e.getMessage());
                return null;
            }

        }

        if(path.equals("/")||path.equals("/index.html")){
            try {
                //return Files.readAllBytes(Paths.get("D:/Codes/KUIT mission/week_2_tomcat/webapp/index.html"));
                return Files.readAllBytes(Paths.get("webapp/index.html"));
            }catch(Exception e){
                System.out.println("winit ork");
                System.out.println("error name "+ e);
                log.log(Level.SEVERE,e.getMessage());
                return null;
            }
        }

        if(path.equals("/webapp/user/form.html")){
            try {
                return Files.readAllBytes(Paths.get("webapp/user/form.html"));
            }catch(Exception e){
                System.out.println("winit ork");
                System.out.println("error name "+ e);
                log.log(Level.SEVERE,e.getMessage());
                return null;
            }
        }
        if(path.equals("/user/signup")){
            System.out.println("user signup start : ["+method+"]");
            if(method.equals("GET")){
                if(query!=null) {
                    User user = getUser(query);
                    //User user = new User(query.get("Id"), query.get("PassWord"), query.get("Name"), query.get("Email"));
                    DB.addUser(user);
                }else{
                    System.out.println("query is null");
                }

            }
            if(method.equals("POST")){
                System.out.println(" POST method start:"+body);
                if(body!=null) {
                    User user = getUser( HttpRequestUtils.parseQueryParameter(body));
                    // = new User(query.get("Id"), query.get("PassWord"), query.get("Name"), query.get("Email"));
                    DB.addUser(user);
                }else{
                    System.out.println("query is null");
                }

            }

            System.out.println(" method task end");
            try {
                redirection.append("/index.html");
                return Files.readAllBytes(Paths.get("webapp/index.html"));
            }catch(Exception e){
                System.out.println("rout error");
                System.out.println("error name "+ e);
                log.log(Level.SEVERE,e.getMessage());
                return null;
            }


        }

        if(path.equals("/user/login")||path.equals("/webapp/user/login.html")){
            System.out.println("login API start");
            System.out.println("method : "+method);
            System.out.println("query : "+query);
            System.out.println("body : "+body);

            if(method.equals("GET")){

                try {
                    return Files.readAllBytes(Paths.get("webapp/user/login.html"));
                }catch(IOException e){
                    System.out.println("rout error");
                    System.out.println("error name "+ e);
                    log.log(Level.SEVERE,e.getMessage());
                }


            }
            if(method.equals("POST")){
                System.out.println(" POST body start:"+body);
                if(body!=null) {
                    Map<String, String> User =HttpRequestUtils.parseQueryParameter(body);
                    String Id=User.get("userId");

                    System.out.println(" login fail:");

                    User targetUser = DB.findUserById(Id);
                    if(targetUser==null){
                        //로그인 실패
                        System.out.println(" login fail:");
                        try {
                            return Files.readAllBytes(Paths.get("webapp/user/login_failed.html"));
                        }catch(IOException e){
                            System.out.println("rout error");
                            System.out.println("error name "+ e);
                            log.log(Level.SEVERE,e.getMessage());
                        }
                    }
                    String rightPassword =DB.findUserById(Id).getPassword();
                    System.out.println(" parse login body");
                    if(rightPassword.equals(User.get("password"))){
                        //로그인 성공
                        System.out.println(" login success:");
                        try {
                            //캐시 발행
                            headAdder.append("Set-Cookie: logined=true; Path=/\r\n");
                            redirection.append("/index.html");
                            return Files.readAllBytes(Paths.get("webapp/index.html"));
                        }catch(IOException e){
                            System.out.println("rout error");
                            System.out.println("error name "+ e);
                            log.log(Level.SEVERE,e.getMessage());
                        }
                    }else{
                        //로그인 실패
                        System.out.println(" login fail:");
                        try {
                            return Files.readAllBytes(Paths.get("webapp/user/login_failed.html"));
                        }catch(IOException e){
                            System.out.println("rout error");
                            System.out.println("error name "+ e);
                            log.log(Level.SEVERE,e.getMessage());
                        }

                    }
                }else{
                    System.out.println("body is null");
                }

            }

        }
        if(path.equals("/user/userList")){
            if(requestHeader.get("Cookie")==null){
                try {
                    System.out.println("cant list in");
                    redirection.append("/index.html");
                    return Files.readAllBytes(Paths.get("webapp/index.html"));
                }catch(Exception e){
                    System.out.println("winit ork");
                    System.out.println("error name "+ e);
                    log.log(Level.SEVERE,e.getMessage());
                    return null;
                }
            }
            if(requestHeader.get("Cookie")!=null || requestHeader.get("Cookie").equals("logined=true")){
                try {
                    return Files.readAllBytes(Paths.get("webapp/user/list.html"));
                }catch(Exception e){
                    System.out.println("winit ork");
                    System.out.println("error name "+ e);
                    log.log(Level.SEVERE,e.getMessage());
                    return null;
                }
            }else{
                try {
                    System.out.println("cant list in");
                    redirection.append("/index.html");
                    return Files.readAllBytes(Paths.get("webapp/index.html"));
                }catch(Exception e){
                    System.out.println("winit ork");
                    System.out.println("error name "+ e);
                    log.log(Level.SEVERE,e.getMessage());
                    return null;
                }
            }

        }


        return null;
    }
    private Map<String,String> parseUserBody(String body){
        Map<String,String> result=null;
        String[] pairList;
        String[] pair;
        pairList = body.split("&");
        for(String pairTemp :pairList) {
            pair = pairTemp.split("=");
            result.put(pair[0].trim(),pair[1].trim());
            System.out.println("("+pair[0]+","+pair[1]+")");
        }
        return result;
    }
    private User getUser(Map<String,String> userMap){
        User user = new User(userMap.get("userId"),userMap.get("password"),userMap.get("name"),userMap.get("email"));
        return user;
    }
    private Map<String,String> getHeader(BufferedReader br/*, Map<String,String> reqHeader*/){
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

    private byte[] readHTMLFile(File file) throws IOException{
        byte[] body;
        try (FileInputStream fis = new FileInputStream(file)){
            body = new byte[(int) file.length()];
            fis.read(body);
        }
        return body;
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