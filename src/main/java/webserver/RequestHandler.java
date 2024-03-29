package webserver;

import db.MemoryUserRepository;
import domain.HttpMethod;
import domain.URL;
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

import static domain.HttpMethod.*;
import static domain.URL.*;
import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final MemoryUserRepository memoryUserRepository;

    private final Path homePath = Paths.get(ROOT_URL.getValue() + HOME_URL.getValue());

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

            if(method.equals(GET.getMethod()) && url.endsWith(".html")){
                body = Files.readAllBytes(Paths.get(ROOT_URL.getValue() + url));
            }

            if(method.equals(GET.getMethod()) && url.startsWith("/user/signup")){
                String queryString = url.split("\\?")[1];
                Map<String,String> queryParameter = parseQueryParameter(queryString);
                User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
                memoryUserRepository.addUser(user);
                response302Header(dos, HOME_URL.getValue());
                return;
            }

            if(method.equals(POST.getMethod()) && url.startsWith("/user/signup")){
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String,String> queryParameter = parseQueryParameter(queryString);
                User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
                memoryUserRepository.addUser(user);
                response302Header(dos, HOME_URL.getValue());
                return;
            }

            if(method.equals(POST.getMethod()) && url.equals("/user/login")){
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String,String> queryParameter = parseQueryParameter(queryString);
                User user = memoryUserRepository.findUserById(queryParameter.get("userId"));
                if(user != null && user.getPassword().equals(queryParameter.get("password"))){
                    response302HeaderWithCookie(dos, HOME_URL.getValue());
                    return;
                }else{
                    response302Header(dos, LOGIN_FAILED_URL.getValue());
                }
            }

            if (url.equals("/user/userList")){
                if(cookie.equals("logined=true")){
                    body = Files.readAllBytes(Paths.get(ROOT_URL.getValue() + LIST_URL.getValue()));
                }else{
                    response302Header(dos, LOGIN_URL.getValue());
                }
            }

            if (method.equals(GET.getMethod()) && url.endsWith(".css")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL.getValue() + url));
                response200HeaderWithCss(dos, body.length);
                responseBody(dos, body);
                return;
            }

            if (method.equals(GET.getMethod()) && url.endsWith(".jpeg")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL.getValue() + url));
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