package webserver;

import db.MemoryUserRepository;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static db.MemoryUserRepository.getInstance;
import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());


    final String basic_url = "./webapp";
    final String index_html_url = basic_url +"/index.html";
    final String user_userList_url = basic_url +"/user/list.html";
    final String css_url = basic_url +"/css/styles.css";
    /*
    final String user_form_url = basic_url +"/user/form.html";
    final String user_login_url = basic_url +"/user/login.html";
    final String user_login_failed_url = basic_url +"/user/login_failed.html";
    */



    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream();){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            MemoryUserRepository db_user = getInstance();

            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String method = startLines[0];
            String url = startLines[1];

            log.log(Level.INFO, startLine + "\n");

            int requestContentLength = 0;
            boolean cookie = false;


            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                // cookie info
                if (line.startsWith("Cookie: logined=true")) {
                    log.log(Level.INFO, "test1\n");
                    cookie = true;
                }
            }

            //예시 코드에서는 바로 return해주는 방법을 사용하고 있었음. -> 아하!
            // GET
            if(method.equals("GET")){

                // 홈
                if(url.equals("/") || url.equals("/index.html")){
                    byte[] body = Files.readAllBytes(Paths.get(index_html_url));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                    return;
                }

                /*
                // 회원가입 화면
                if(url.equals("/user/form.html")){
                    byte[] body = Files.readAllBytes(Paths.get(user_form_url));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }

                // 로그인 실패 화면
                if(url.equals("/login_failed.html")){
                    byte[] body = Files.readAllBytes(Paths.get(user_login_failed_url));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }
                // 로그인 화면
                if(url.equals("/user/login.html")){
                    byte[] body = Files.readAllBytes(Paths.get(user_login_url));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }
                */

                // 예시 코드에 있던 방법. 아주 좋네요
                if(url.endsWith(".html")){
                    byte[] body = Files.readAllBytes(Paths.get(basic_url + url));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                    return;
                }



                // 회원가입 요청 GET
                if(url.matches("^/user/signup.*")){
                    Map<String, String> user_info = parseQueryParameter(url);
                    User user = new User(user_info.get("userId"), user_info.get("password"), user_info.get("name"), user_info.get("email"));
                    db_user.addUser(user);
                    response302Header(dos, "/index.html");
                    return;
                }




                // 유저 목록 화면
                if(url.equals("/user/userList")){
                    if(cookie){
                        byte[] body = Files.readAllBytes(Paths.get(user_userList_url));
                        response200Header(dos, body.length);
                        responseBody(dos, body);
                    } else {
                        response302Header(dos, "/user/login.html");
                    }
                    return;
                }


                // css 요청시 반환
                if(url.endsWith(".css")){
                    byte[] body = Files.readAllBytes(Paths.get(css_url));
                    response200HeaderCss(dos, body.length);
                    responseBody(dos, body);
                    return;
                }
            }

            // POST
            if(method.equals("POST")){
                // 회원가입 요청 POST
                if(url.equals("/user/signup")){
                    Map<String, String> user_info = parseQueryParameter(readData(br, requestContentLength));
                    User user = new User(user_info.get("userId"), user_info.get("password"), user_info.get("name"), user_info.get("email"));
                    db_user.addUser(user);
                    response302Header(dos, "/index.html");
                    return;

                }




                // 로그인 요청
                if(url.equals("/user/login")){
                    Map<String, String> user_info = parseQueryParameter(readData(br, requestContentLength));
                    User user = db_user.findUserById(user_info.get("userId"));

                    if (user != null && user.getPassword().equals(user_info.get("password"))) {
                        response302HeaderWithCookie(dos,"/index.html");
                    } else {
                        response302Header(dos, "/login_failed.html");
                    }
                    return;
                }



            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response200HeaderCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }


    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }


    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}