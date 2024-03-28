package webserver;

import db.Repository;
import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
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
        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            //헤더
            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String method = startLines[0];
            String url = startLines[1];

            byte[] body=new byte[0];

            //1-1 :  index.html 반환하기
            if (method.equals("GET")&& url.endsWith(".html")) {
                body = Files.readAllBytes(Paths.get(Paths.get("./webapp"+ url).toUri()));
            }
            if (url.equals("/")) {
                body = Files.readAllBytes(Path.of("./webapp/index.html"));
            }

            //1-2 : GET 방식으로 회원가입하기
            if(url.contains("/user/signup") && method.equals("GET")){
                // 쿼리 스트링 정보를 파싱
                String[] str = url.split("\\?");
                String queryString = str[1];

                Map<String, String> queryParameters = HttpRequestUtils.parseQueryParameter(queryString);
                // User 생성
                User user = new User(queryParameters.get("userId"), queryParameters.get("password"),
                        queryParameters.get("name"), queryParameters.get("email"));

                // 사용자 정보를 MemoryUserRepository에 저장
                Repository MemoryUserRepository;
                repository.addUser(user);

                //index.html로 리다이렉션
                response302Header(dos, "/index.html");
            }

            //1-3 : POST 방식으로 회원가입하기
            int contentLength = 0;
            if(url.equals("/user/signup")){
                String queryString = IOUtils.readData(br, contentLength);
                Map<String, String> queryParameters = HttpRequestUtils.parseQueryParameter(queryString);

                // User 생성
                User user = new User(queryParameters.get("userId"), queryParameters.get("password"),
                        queryParameters.get("name"), queryParameters.get("email"));

                // 사용자 정보를 MemoryUserRepository에 저장
                Repository MemoryUserRepository;
                repository.addUser(user);

                //index.html로 리다이렉션
                response302Header(dos, "/index.html");
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
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
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