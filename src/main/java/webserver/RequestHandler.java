package webserver;

import db.MemoryUserRepository;
import db.Repository;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String ROOT_URL = "./webapp";
    private static final String HOME_URL = "/index.html";
    private final Repository repository;
    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        //log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String method = startLines[0];
            String url = startLines[1];

            int requestContentLength = 0;
            String requestBody = "";
            Map<String, String> parsedRequestBody = null;
            String[] cookies = new String[0];
            while (true) {
                final String line = br.readLine();
                if (line.isEmpty()) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                if (line.startsWith("Cookie")) {
                    String cookieString = line.split(": ")[1];
                    cookies = cookieString.split("; ");

                }
            }
            if (requestContentLength!=0) {
                requestBody = readData(br, requestContentLength);
                parsedRequestBody = parseQueryParameter(requestBody);
            }

            log.log(Level.INFO, "method: " + method + ", url: " + url);
            byte[] body = new byte[0];

            if(url.equals("/")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + HOME_URL));
            }

            if(method.equals("GET") && url.endsWith(".html")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
            }

            if(method.equals("POST") && url.equals("/user/signup")) {
                repository.addUser(new User(parsedRequestBody.get("userId"), parsedRequestBody.get("password"), parsedRequestBody.get("name"), parsedRequestBody.get("email")));
                log.log(Level.INFO, "saved " + repository.findUserById(parsedRequestBody.get("userId")).toString());
                response302Header(dos, ".."+HOME_URL); //TODO 상대경로 절대경로 해결하기
            }

            if(method.equals("POST") && url.equals("/user/login")) {
                User foundUser = repository.findUserById(parsedRequestBody.get("userId"));
                if(foundUser!=null && foundUser.getPassword().equals(parsedRequestBody.get("password"))) {
                    response302HeaderWithCookie(dos, ".."+HOME_URL, "logined=true; path=/;");
                } else {
                    response302Header(dos, "./login_failed.html"); //TODO 상대경로 절대경로 해결하기
                }
            }

            if(url.equals("/user/userList")) {
                if(!Arrays.asList(cookies).contains("logined=true")) {
                    response302Header(dos,"/user/login.html");
                    return;
                }
                body = Files.readAllBytes(Paths.get(ROOT_URL + "/user/list.html"));
            }

            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String location, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
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