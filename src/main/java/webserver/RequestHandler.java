package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String ROOT_URL = "./webapp";
    private static final String HOME_URL = "/index.html";
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
            String method = startLines[0];
            String url = startLines[1];
            log.log(Level.INFO, "method: " + method + ", url: " + url);
            // 정보: method: GET, url: /
            // 정보: method: GET, url: /favicon.ico -> favicon은 브라우저에 띄울 웹사이트 아이콘으로 브라우저에서 요청!

            //byte[] body = "Hello World".getBytes();
            byte[] body = new byte[0];

            if(url.equals("/") || method.equals("GET") && url.equals("/index.html")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + HOME_URL));
            }

            if(method.equals("GET") && url.equals("/user/form.html")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
            }

            if(method.equals("POST") && url.equals("/user/signup")) {
                int requestContentLength = 0;
                while (true) {
                    final String line = br.readLine();
                    if (line.equals("")) {
                        break;
                    }
                    // header info
                    if (line.startsWith("Content-Length")) {
                        requestContentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                }
                String postRequestBody = readData(br, requestContentLength);
                Map<String, String> map = parseQueryParameter(postRequestBody);
                Repository repository = MemoryUserRepository.getInstance();
                repository.addUser(new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email")));
                log.log(Level.INFO, "saved " + repository.findUserById(map.get("userId")).toString());
                response302Header(dos, ".."+HOME_URL); //TODO 상대경로 절대경로 해결하기
            }

            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }
    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location);
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