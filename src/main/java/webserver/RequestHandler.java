package webserver;

import db.MemoryUserRepository;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;

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

            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String method = startLines[0]; //GET POST ...
            String url = startLines[1]; ///login.html /index.html /user/form.html...
            //main 화면
            if (url.equals("/index.html") || url.equals("/")) {
                //index.html읽어서 byte[]로 만들기
                //상대경로
                File indexFile = new File("webapp/index.html");
                byte[] body = Files.readAllBytes(indexFile.toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
            //login 화면
            if (method.equals("GET") && url.equals("/user/form.html")){
                File userFormFile = new File("webapp/user/form.html");
                byte[] body = Files.readAllBytes(userFormFile.toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
            //user가 정보를 입력하고 signup버튼을 눌렀을때
            if (method.equals("GET") && url.contains("/user/signup")){
                //userInfo Memory에 저장
                String querySubstring = url.substring(url.indexOf("?")+1);
                Map<String, String> userInfo = parseQueryParameter(querySubstring);
                String userId = userInfo.get("userId");
                String password = userInfo.get("password");
                String name = userInfo.get("name");
                String email = userInfo.get("email");
                User user = new User(userId, password, name, email);
                MemoryUserRepository db = MemoryUserRepository.getInstance(); //싱글톤
                db.addUser(user);

                //redirect 시키기
                response302Header(dos, "/index.html");
            }
            if (method.equals("POST") && url.equals("/user/signup")){
                int requestContentLength = 0;
                while (true) {
                    final String line = br.readLine();
                    //log.log(Level.INFO,"check point : " + line);
                    //***
                    //Host: localhost
                    //Connection: keep-alive
                    //Content-Length: 77
                    //Cache-Control: max-age=0
                    //sec-ch-ua: "Google Chrome";v="123", "Not:A-Brand";v="8", "Chromium";v="123"
                    //sec-ch-ua-mobile: ?0
                    //sec-ch-ua-platform: "macOS"
                    //Upgrade-Insecure-Requests: 1
                    //Origin: http://localhost
                    //Content-Type: application/x-www-form-urlencoded
                    //User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36
                    //Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
                    //Sec-Fetch-Site: same-origin
                    //Sec-Fetch-Mode: navigate
                    //Sec-Fetch-User: ?1
                    //Sec-Fetch-Dest: document
                    //Referer: http://localhost/user/form.html
                    //Accept-Encoding: gzip, deflate, br, zstd
                    //Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
                    //Cookie: _xsrf=2|b0db7321|d7db5fdf21657b9746bdf8d95f1d1c4b|1709786747; username-localhost-8888="2|1:0|10:1711000933|23:username-localhost-8888|44:YjdiNzg1Njg4MjAyNDZjOThjMDNlMjJmY2MwNmNjZGI=|c08046ec9f5629558200765478cc011a4706d563a76fa8a94e3b4625d00ffe2c"
                    //***
                    if (line.equals("")) {
                        break;
                    }
                    // header info
                    if (line.startsWith("Content-Length")) {
                        requestContentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                }
                String messageBody = IOUtils.readData(br, requestContentLength);
                Map<String, String> userInfo = parseQueryParameter(messageBody);
                String userId = userInfo.get("userId");
                String password = userInfo.get("password");
                String name = userInfo.get("name");
                String email = userInfo.get("email");
                User user = new User(userId, password, name, email);
                MemoryUserRepository db = MemoryUserRepository.getInstance(); //싱글톤
                db.addUser(user);

                //redirect 시키기
                response302Header(dos, "/index.html");
            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }
    private void response302Header(DataOutputStream dos, String path){
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path);
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