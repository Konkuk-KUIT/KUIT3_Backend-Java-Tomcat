package webserver;

import db.MemoryUserRepository;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final MemoryUserRepository memoryUserRepository;

    private static final String ROOT_URL = "./webapp";
    private static final String HOME_URL = "/index.html";
    private static final String LOGIN_FAILED_URL = "/user/login_failed.html";
    private static final String LOGIN_URL = "/user/login.html";
    private static final String LIST_URL = "/user/list.html";

    private final Path homePath = Paths.get(ROOT_URL + HOME_URL);

    public RequestHandler(Socket connection) {
        this.connection = connection;
        this.memoryUserRepository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String method = startLines[0];
            String url = startLines[1];

            byte[] body = new byte[0];
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
                if (line.startsWith("Cookie")){
                    cookie = line.split(": ")[1];
                }
            }

            if(url.equals("/")){
                body = Files.readAllBytes(homePath);
            }

            if(method.equals("GET") && url.endsWith(".html")){
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
            }

            if(method.equals("GET") && url.startsWith("/user/signup")){
                String queryString = url.split("\\?")[1];
                Map<String,String> queryMap = parseQueryParameter(queryString);
                User user = new User(queryMap.get("userId"), queryMap.get("password"), queryMap.get("name"), queryMap.get("email"));
                memoryUserRepository.addUser(user);
                response302Header(dos,HOME_URL);
                return;
            }

            if(method.equals("POST") && url.startsWith("/user/signup")){
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String,String> queryMap = parseQueryParameter(queryString);
                User user = new User(queryMap.get("userId"), queryMap.get("password"), queryMap.get("name"), queryMap.get("email"));
                memoryUserRepository.addUser(user);
                response302Header(dos,HOME_URL);
                return;
            }

            if(method.equals("POST") && url.equals("/user/login")){
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String,String> queryMap = parseQueryParameter(queryString);
                User user = memoryUserRepository.findUserById(queryMap.get("userId"));
                if(user != null && user.getPassword().equals(queryMap.get("password"))){
                    response302HeaderWithCookie(dos,HOME_URL);
                    return;
                }else{
                    response302Header(dos, LOGIN_FAILED_URL);
                }
            }

            if (url.equals("/user/userList")){
                if(cookie.equals("logined=true")){
                    body = Files.readAllBytes(Paths.get(ROOT_URL + LIST_URL));
                }else{
                    response302Header(dos, LOGIN_URL);
                }
            }

            if (method.equals("GET") && url.endsWith(".css")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
                response200HeaderWithCss(dos, body.length);
                responseBody(dos, body);
                return;
            }

            if (method.equals("GET") && url.endsWith(".jpeg")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            response200Header(dos, body.length);
            responseBody(dos, body);

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
            dos.flush();
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

    private void response302Header(DataOutputStream dos, String path){
        try{
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        }catch (IOException e){
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path){
        try{
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true" + "\r\n");        //그냥 Cookie로 하면 안되는 이유??
            dos.writeBytes("\r\n");
            dos.flush();
        }catch (IOException e){
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}