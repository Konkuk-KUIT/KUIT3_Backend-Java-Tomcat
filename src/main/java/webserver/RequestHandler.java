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

public class RequestHandler implements Runnable {
    private final Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String ROOT_URL = "./webapp";
    private static final String LIST_URL = "/user/list.html";

    private final Repository repository;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        this.repository = MemoryUserRepository.getInstance();
    }




    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            // Header 분석
            String startLine = br.readLine();

            String[] startLines = startLine.split(" ");
            String method = startLines[0];
            String url = startLines[1];
            System.out.println("url = " + url);

            byte[] body = new byte[0];
            // 404 처리
            if (!method.equals("GET") && !method.equals("POST")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + "/error/404.html"));
                response404Header(dos, body.length);
                responseBody(dos, body);
                return;
            }



            // GET 요청 처리
            if ("GET".equals(method)) {
                if ("/".equals(url) || "/index.html".equals(url)) {
                    body = Files.readAllBytes(Paths.get("webapp/index.html"));
                }
                // CSS 파일 요청 처리
                else if (url.endsWith(".css")) {
                    body = Files.readAllBytes(Paths.get(ROOT_URL + url));
                    response200HeaderWithContentType(dos, body.length, "text/css");
                    responseBody(dos, body);
                    return;
                }
                else if("/user/form.html".equals(url)) {
                    body = Files.readAllBytes(Paths.get("webapp/user/form.html"));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                    return;
                }
                // userList 페이지 요청 처리

            }

            // POST 요청 처리
            if (method.equals("POST")) {
                System.out.println("포스트 요청이 왔어요");
                // 회원가입 요청 처리

                if (url.equals("/user/signup")) {
                    System.out.println("좋아요");
                    int requestContentLength = 0;
                    String line = "1";
                    // 헤더 읽기
                    while (true) {// HTTP 요청의 헤더 부분을 읽어서 요청 바디의 길이를 파악
                        line = br.readLine();
                        if (line.equals("")) { //빈 줄을 만나면 헤더 부분의 읽기를 중지
                            break;
                        }
                        if (line.startsWith("Content-Length")) {
                            requestContentLength = Integer.parseInt(line.split(": ")[1]); //Content-Length 헤더의 값을 파싱하여 contentLength 변수에 저장. ex) 'Content-Length: 1234' 에서 1234 추출
                        }
                    }

                    String requestBody = IOUtils.readData(br, requestContentLength);
                    System.out.println("여기는 실행이 될까요?");
                    Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(requestBody);
                    User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
                    repository.addUser(user);
                    response302Header(dos, "/index.html");
                    return;
                }

            }

            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage());
            }
        }
    }


    private int getContentLength(BufferedReader br) throws IOException {
        int contentLength = 0;
        while (true) {
            String line = br.readLine();
            if (line == null || line.equals("")) {
                break;
            }
            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }
        return contentLength;
    }



    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }


    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK\r\n");
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    }

    private void response200HeaderWithContentType(DataOutputStream dos, int lengthOfBodyContent, String contentType) throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK\r\n");
        dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    }

    private void responseBody(DataOutputStream dos, byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.flush();
    }

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) throws IOException {
        dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
        dos.writeBytes("Content-Type: text/plain;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
        dos.writeBytes("404 Not Found\r\n");
    }
}
