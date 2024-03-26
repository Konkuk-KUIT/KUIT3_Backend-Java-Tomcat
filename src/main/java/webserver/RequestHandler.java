package webserver;

import db.MemoryUserRepository;
import db.Repository;
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
import static webserver.HttpHeader.*;
import static webserver.HttpMethod.GET;
import static webserver.HttpMethod.POST;
import static webserver.UrlPath.*;
import static webserver.UserQueryKey.*;


public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final Repository repository;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = new byte[0];

            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            HttpMethod method = HttpMethod.valueOf(startLines[0]);;
            String url = startLines[1];


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
                    System.out.println("request : "+requestContentLength);
                }

                if (line.startsWith("Cookie")) {
                    cookie = line.split(": ")[1];
                }
            }
            // 요구 사항 1
            if (method == GET && url.endsWith(".html")) {
                Path path = Paths.get(ROOT.getPath() + url);
                body = Files.readAllBytes(path);
            }

            if (url.equals("/")) {
                body = Files.readAllBytes(getHomePath());
            }
            //요구 사항 2
            if(method.equals(GET) && url.equals("/user/form.html")){
                Path path = Paths.get(ROOT.getPath() + url);
                body = Files.readAllBytes(path);
            }
            if(method.equals(GET) && url.startsWith("/user/signup")){
                String UserInfo = url.split("\\?")[1];
                Map<String,String> queryParameter = parseQueryParameter(UserInfo);
                String userId = queryParameter.get(USER_ID.getKey());
                String password = queryParameter.get(PASSWORD.getKey());
                String name = queryParameter.get(NAME.getKey());
                String email = queryParameter.get(EMAIL.getKey());
                User user = new User(userId, password, name, email);
                repository.addUser(user);
                response302Header(dos, INDEX.getPath());
            }
            //요구 사항 3
            if(method.equals(POST) && url.equals("/user/signup")){
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                String userId = queryParameter.get(USER_ID.getKey());
                String password = queryParameter.get(PASSWORD.getKey());
                String name = queryParameter.get(NAME.getKey());
                String email = queryParameter.get(EMAIL.getKey());
                User user = new User(userId, password, name, email);
                repository.addUser(user);
                response302Header(dos,INDEX.getPath());
            }
            //요구 사항 5
            if (url.equals("/user/login")) {
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                String userId = queryParameter.get(USER_ID.getKey());
                User user = repository.findUserById(userId);
                login(dos, queryParameter, user);
                return;
            }

            // 요구 사항 6
            if (url.equals("/user/userList")) {
                if (!cookie.equals("logined=true")) {
                    response302Header(dos,LOGIN.getPath());
                    return;
                }
                body = Files.readAllBytes(Paths.get(ROOT.getPath() + LIST.getPath()));
            }

            // 요구 사항 7번
            if (method.equals(GET) && url.endsWith(".css")) {
                body = Files.readAllBytes(Paths.get(ROOT.getPath() + url));
                response200HeaderWithCss(dos, body.length);
                responseBody(dos, body);
                return;
            }

            // image
            if (method.equals(GET) && url.endsWith(".jpeg")) {
                body = Files.readAllBytes(Paths.get(ROOT.getPath() + url));
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
    private void login(DataOutputStream dos, Map<String, String> queryParameter, User user) {
        if (user != null && user.getPassword().equals(queryParameter.get(PASSWORD.getKey()))) {
            response302HeaderWithCookie(dos,INDEX.getPath());
            return;
        }
        response302Header(dos,LOGIN_FAILED.getPath());
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

    private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes(CONTENT_TYPE.getHeader() + ": text/css;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.getHeader() + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes(LOCATION.getHeader()+ ": " + path + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes(LOCATION.getHeader() + ": " + path + "\r\n");
            dos.writeBytes(SET_COOKIE.getHeader() + ": logined=true" + "\r\n");
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

}