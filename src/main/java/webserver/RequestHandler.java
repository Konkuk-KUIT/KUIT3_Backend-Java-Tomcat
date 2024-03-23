package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static http.util.HttpRequestUtils.parseQueryParameter;

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

            // input stream 확인
            // ex)
            // GET /index.html HTTP/1.1 이런 형식으로 올거임
            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String method = startLines[0];
            String url = startLines[1];

            byte[] body = new byte[0];

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

            // 요구사항 1
            // / 이거나 index.html이면 바디에 해당 파일을 넘겨야함.
            if(method.equals("GET") && url.equals("/index.html")){
                body = Files.readAllBytes(Paths.get("./webapp" + url));
            }

            if(url.equals("/")){
                body = Files.readAllBytes(Paths.get("./webapp/index.html"));
            }

            // 요구사항 2
            //  SignUp 버튼을 클릭하면 /user/form.html 화면으로 이동
            if(method.equals("GET") && url.equals("/user/form.html")){
                body = Files.readAllBytes(Paths.get("./webapp" + url));
            }
            // /user/signup
            if(method.equals("GET") && url.startsWith("/user/signup")){
                String tmp = url.split("\\?")[1];
                Map<String,String> m = parseQueryParameter(tmp);
                User user = new User(m.get("userId"), m.get("password"), m.get("name"), m.get("email"));
                repository.addUser(user);
                response302Header(dos,"/index.html");
                return;
            }

            // 요구사항 3
            if(method.equals("POST") && url.equals("/user/signup")){
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String,String> m = parseQueryParameter(queryString);
                User user = new User(m.get("userId"), m.get("password"), m.get("name"), m.get("email"));
                repository.addUser(user);
                response302Header(dos,"/index.html");
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

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
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