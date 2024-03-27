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
import static webserver.Url.*;


public class RequestHandler implements Runnable{

    Socket connection;



    private final Repository repository;



    private final Path homePath = Paths.get(ROOT_URL.getUrl() + HOME_URL.getUrl());


    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        //try-with-resources 구문사용
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = new byte[0];



            //서버로 부터 오는 Header 분석하는 부분 //////////
            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            //startLines : ["GET", "/", "HTTP/1.1"]
            String method = startLines[0];
            //method : "GET"
            String url = startLines[1];
            //url : "/"

            int requestContentLength = 0;
            String cookie = "";

            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }

                if (line.startsWith("Cookie")) {
                    cookie = line.split(": ")[1];
                }
            }


            //서버에서 요청이 GET이고 .html로 끝난다면
            if (method.equals("GET") && url.endsWith(".html")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL.getUrl() + url));
            }

            if (url.equals("/")) {
                body = Files.readAllBytes(homePath);
            }



            //회원가입 submit제출했을 시
            if (url.equals("/user/signup") ) {
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
                repository.addUser(user);
                System.out.println("user : "+user.getName());
                response302Header(dos,HOME_URL.getUrl());

                return;
            }

            //요구사항 5번
            if (url.equals("/user/login")) {
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                User user = repository.findUserById(queryParameter.get("userId"));
                login(dos, queryParameter, user);
                return;
            }

            //요구사항6번
            if (url.equals("/user/userList")) {
                if (!cookie.equals("logined=true")) {
                    response302Header(dos,LOGIN_URL.getUrl());
                    return;
                }
                body = Files.readAllBytes(Paths.get(ROOT_URL.getUrl() + LIST_URL.getUrl()));
            }

            //요구사항7번
            if (method.equals("GET") && url.endsWith(".css")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL.getUrl() + url));
                response200HeaderWithCss(dos, body.length);
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
            dos.writeBytes(HTTP200.getHeader());
            dos.writeBytes(ContextTypeHtml.getHeader());
            dos.writeBytes(ContentLength.getHeader() + lengthOfBodyContent + "\r\n");
            dos.writeBytes(Escape.getHeader());
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void login(DataOutputStream dos, Map<String, String> queryParameter, User user) {
        if (user != null && user.getPassword().equals(queryParameter.get("password"))) {
            response302HeaderWithCookie(dos,HOME_URL.getUrl());
            return;
        }
        response302Header(dos,LOGIN_FAILED_URL.getUrl());
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response302Header(DataOutputStream dos, String path) {
        //302(임시 이동): 현재 서버가 다른 위치의 페이지로 요청에 응답하고 있지만 요청자는 향후 요청 시 원래 위치를 계속 사용해야 한다.
        try {
            dos.writeBytes(HTTP302.getHeader());
            dos.writeBytes( Location.getHeader()+ path + Escape.getHeader());
            dos.writeBytes(Escape.getHeader());
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes(HTTP302.getHeader() );
            dos.writeBytes(Location.getHeader() + path + Escape.getHeader());
            dos.writeBytes(SetCookie.getHeader() + Escape.getHeader());
            dos.writeBytes(Escape.getHeader());
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes(HTTP200.getHeader());
            dos.writeBytes(ContextTypeCss.getHeader());
            dos.writeBytes(ContentLength.getHeader() + lengthOfBodyContent +Escape.getHeader());
            dos.writeBytes(Escape.getHeader());
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}