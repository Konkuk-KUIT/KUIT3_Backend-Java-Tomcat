package webserver;

import db.MemoryUserRepository;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String method = startLines[0]; //GET POST ...
            String url = startLines[1]; ///login.html /index.html /user/form.html...
            //main 화면
            if (url.equals("/index.html") || url.equals("/")) {
                //index.html읽어서 byte[]로 만들기
                //상대경로
                File indexFile = new File("webapp/index.html");
                byte[] body = Files.readAllBytes(indexFile.toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
            //login 화면
            if (method.equals("GET") && url.equals("/user/form.html")){
                File userFormFile = new File("webapp/user/form.html");
                log.log(Level.SEVERE,"method : " + method);
                byte[] body = Files.readAllBytes(userFormFile.toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
            //user가 정보를 입력하고 signup버튼을 눌렀을때
            if (method.equals("GET") && url.contains("/user/signup")){
                //여기서 원래는 GET이어서 querystrign에 user정보가 ?뒤에 들어가있어야하는데 왜 method는 POST로, user정보는 body로 구현되어있지?
                //log.log(Level.INFO, "this is " + method +" method");
                //INFO: this is POST method
                //form.html에 있는 method = "GET"으로 바꾸어야 제대로 queryString에 user정보가 들어옴
                ///user/signup?userId=annotatio901&password=2001&name=kim+joohye&email=annie2104%40naver.com

                //userInfo Memory에 저장
                String querySubstring = url.substring(url.indexOf("?")+1);
                Map<String, String> userInfo = parseQueryParameter(querySubstring);
                String userId = userInfo.get("userId");
                String password = userInfo.get("password");
                String name = userInfo.get("name");
                String email = userInfo.get("email");
                User user = new User(userId, password, name, email);
                MemoryUserRepository db = MemoryUserRepository.getInstance(); //싱글톤
                db.addUser(user);

                //redirect 시키기
                response302Header(dos, "/index.html");
            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }
    private void response302Header(DataOutputStream dos, String path){
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path);
            dos.writeBytes("\r\n");
        } catch (IOException e) {
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