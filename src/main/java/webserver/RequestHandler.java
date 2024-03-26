package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import javax.print.DocFlavor;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    private static final String PATH = "C:\\kuit\\KUIT3_Backend-Java-Tomcat\\webapp\\";
    Socket connection;
    private final MemoryUserRepository memoryUserRepository;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
        this.memoryUserRepository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        String url;
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);
            // Header 분석
            String startLine = br.readLine();
            System.out.println(startLine);
            String[] startLines = startLine.split(" ");
            String method = startLines[0];
            url = startLines[1];

            int requestContentLength = 0;
            String cookie = "";

            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                if (line.startsWith("Cookie")) {
                    cookie = line.split(": ")[1];
                    System.out.println(cookie);
                }
            }
            Path path = Paths.get(PATH + url);
            byte[] body = new byte[0];
            if (method.equals("GET") && url.endsWith(".html")) {
                body = Files.readAllBytes(path);
            }
            if(url.equals("/user/signup")){
                //body 부분
                String UserInfoquery = IOUtils.readData(br,requestContentLength);
                //System.out.println(UserInfoquery);
                Map<String,String> UserMap = HttpRequestUtils.parseQueryParameter(UserInfoquery);
                User user = new User(UserMap.get("userId"),UserMap.get("password"),UserMap.get("name"),UserMap.get("email"));
                memoryUserRepository.addUser(user);
                response302Header(dos,"/index.html");
            }
            if(url.equals("/user/login")){
                String loginUserQuery = IOUtils.readData(br,requestContentLength);
                System.out.println(loginUserQuery);
                Map<String,String> UserMap = HttpRequestUtils.parseQueryParameter(loginUserQuery);
                if(memoryUserRepository.findUserById(UserMap.get("userId"))==null){ //없는 경우
                    response302Header(dos,"/user/login_failed.html");
                    return;
                }
                response302HeaderWithCookie(dos,"/index.html");
            }
            if(url.equals("/user/userList")){
                if(!cookie.contains("logined=true")){
                    response302Header(dos,"/user/login.html");
                    return;
                }
                Path userListPath = Paths.get(PATH +"/user/list.html");
                body = Files.readAllBytes(userListPath);
            }
            if (method.equals("GET") && url.endsWith(".css")) {
                body = Files.readAllBytes(path);
                response200HeaderWithCss(dos, body.length);
                responseBody(dos, body);
                return;
            }
            response200Header(dos, body.length);
            responseBody(dos, body);

        }
        catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200HeaderWithCss(DataOutputStream dos, int length) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + length + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true" + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header (DataOutputStream dos,int lengthOfBodyContent){
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void responseBody (DataOutputStream dos,byte[] body){
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}