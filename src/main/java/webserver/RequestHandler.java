package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

            String startline = br.readLine();
            String[] startlines = startline.split(" ");
            String method = startlines[0];
            String url = startlines[1];

            String headerLine;
            String cookie = "";
            int contentLength = 0;

            while(true){
                headerLine = br.readLine();
                if(headerLine.equals("")) break;
                if(headerLine.startsWith("Content-Length: ")){
                    String[] line = headerLine.split(" ");
                    contentLength = Integer.parseInt(line[1]);

//                    System.out.println("Content-Length: " + contentLength);
                }
                if(headerLine.startsWith("Cookie: ")){
                    cookie = headerLine.substring(headerLine.indexOf(":") + 1);
                }
            }

            if(method.equals("GET") && url.equals("/user/userList")){
                log.log(Level.INFO, "/user/userList");

                if(cookie.contains("logined=true")){
                    byte[] body = Files.readAllBytes(Paths.get("./webapp/user/list.html"));
                    response200Header(dos, "text/html", body.length);
                    responseBody(dos, body);
                    return;
                }
                response302Header(dos, "/index.html");
                return;
            }

            if(method.equals("GET") && url.startsWith("/user/signup")){
                log.log(Level.INFO, "/user/login GET");

                String[] queryString = url.split("\\?");
                Map<String, String> map = HttpRequestUtils.parseQueryParameter(queryString[1]);

                String userId = map.get("userId");
                String password = map.get("password");
                String name = map.get("name");
                String email = map.get("email");

                User user = new User(userId, password, name, email);
                MemoryUserRepository repository = MemoryUserRepository.getInstance();

                repository.addUser(user);

                response302Header(dos, "/index.html");

                return;
            }

            if(method.equals("GET")){
                log.log(Level.INFO, "GET " + url);

                String contentType = "text/html";
                byte[] body;
                StringBuilder sb = new StringBuilder();

                sb.append("./webapp");
                if(url.equals("/")) {
                    sb.append("/index.html");

                    body = Files.readAllBytes(Paths.get(sb.toString()));

                    response200Header(dos, contentType, body.length);
                    responseBody(dos, body);
                    return;
                }

                sb.append(url);

                body = Files.readAllBytes(Paths.get(sb.toString()));

                if(url.endsWith(".css")){
                    contentType = "text/css";
                }
                response200Header(dos, contentType, body.length);
                responseBody(dos, body);
                return;
            }


            if(method.equals("POST") && url.equals("/user/signup")){
                log.log(Level.INFO, "/user/signup POST");

                String queryString = IOUtils.readData(br, contentLength);
                Map<String, String> map = HttpRequestUtils.parseQueryParameter(queryString);

                String userId = map.get("userId");
                String password = map.get("password");
                String name = map.get("name");
                String email = map.get("email");

                User user = new User(userId, password, name, email);

                MemoryUserRepository repository = MemoryUserRepository.getInstance();

                repository.addUser(user);

//                repository.findAll().forEach( (u)->{ System.out.println("User(id=" + u.getUserId() + ", pwd=" + u.getPassword()); } );

                response302Header(dos, "/index.html");

                return;
            }

            if(method.equals("POST") && url.equals("/user/login")){
                log.log(Level.INFO, "/user/login");

                MemoryUserRepository repository = MemoryUserRepository.getInstance();

                String queryString = IOUtils.readData(br, contentLength);
                Map<String, String> map = HttpRequestUtils.parseQueryParameter(queryString);

                System.out.println(queryString);

                User result = repository.findUserById(map.get("userId"));
                if(result != null && result.getPassword().equals(map.get("password"))){
                    response302HeaderWithCookie(dos, "/index.html");
                }
                else{
                    response302Header(dos, "/user/login_failed.html");
                }
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, String contentType, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType+ ";charset=utf-8\r\n");
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
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
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