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

import static webserver.HttpMethod.GET;
import static webserver.HttpMethod.POST;
import static webserver.Url.*;

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
                }
                if(headerLine.startsWith("Cookie: ")){
                    cookie = headerLine.substring(headerLine.indexOf(":") + 1);
                }
            }

            if(method.equals(GET.get()) && url.equals(USER_LIST.get())){
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

            if(method.equals(GET.get()) && url.startsWith(SIGNUP.get())){
                log.log(Level.INFO, "/user/signup GET");

                String[] queryString = url.split("\\?");

                registerUser(queryString[1]);

                response302Header(dos, "/index.html");

                return;
            }

            if(method.equals(GET.get())){
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


            if(method.equals(POST.get()) && url.equals(SIGNUP.get())){
                log.log(Level.INFO, "/user/signup POST");

                String queryString = IOUtils.readData(br, contentLength);

                registerUser(queryString);

                response302Header(dos, "/index.html");

                return;
            }

            if(method.equals(POST.get()) && url.equals(LOGIN.get())){
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

    private void registerUser(String queryString){
        Map<String, String> map = HttpRequestUtils.parseQueryParameter(queryString);

        String userId = map.get(QueryKey.USER_ID.get());
        String password = map.get(QueryKey.PASSWORD.get());
        String name = map.get(QueryKey.NAME.get());
        String email = map.get(QueryKey.EMAIL.get());

        User user = new User(userId, password, name, email);

        MemoryUserRepository repository = MemoryUserRepository.getInstance();

        if(repository.findUserById(userId) == null){
            repository.addUser(user);
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