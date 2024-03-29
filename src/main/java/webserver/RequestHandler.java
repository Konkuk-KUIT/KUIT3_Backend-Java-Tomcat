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

public class RequestHandler implements Runnable {
    private static final String INDEX_HTML_PATH = "webapp/index.html";
    private static final String USER_FORM_HTML_PATH = "webapp/user/form.html";
    private static final String LOGIN_HTML_PATH = "webapp/user/login.html";
    private static final String LOGIN_FAILED_HTML_PATH = "webapp/user/login_failed.html";
    private static final String USER_LIST_HTML_PATH = "webapp/user/list.html";

    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest from = HttpRequest.from(br);

            String method = from.getMethod();
            String url = from.getUrl();
            //단순히 html만 보여주는 경우에는 따로 swich문으로
            switch (url) {
                case "/index.html":
                case "/":
                    serveFile(INDEX_HTML_PATH, dos);
                    break;
                case "/user/form.html":
                    serveFile(USER_FORM_HTML_PATH, dos);
                    break;
                case "/user/login.html":
                    serveFile(LOGIN_HTML_PATH, dos);
                    break;
                case "/user/login_failed.html":
                    serveFile(LOGIN_FAILED_HTML_PATH, dos);
                    break;
                case "/user/userList":
                    //cookie 값 확인
                    if (from.cookieLogined()) {
                        serveFile(USER_LIST_HTML_PATH, dos);
                    }
                    response302Header(dos, "/user/login.html");
                    break;
                case "/css/styles.css":
                    dos.writeBytes("HTTP/1.1 200 OK \r\n");
                    dos.writeBytes("Content-Type: text/css\r\n");
                    dos.writeBytes("\r\n");
                    dos.flush();
                    break;
                default:
                    log.log(Level.INFO, "Unsupported URL: " + url);
            }

            //user가 정보를 입력하고 signup버튼을 눌렀을때 - method:GET
            if (method.equals("GET") && url.contains("/user/signup")) {
                //userInfo Memory에 저장
                String querySubstring = url.substring(url.indexOf("?") + 1);
                addUserToDB(querySubstring);
                response302Header(dos, "/index.html");
            }
            //user가 정보를 입력하고 signup버튼을 눌렀을때 - method:POST
            if (method.equals("POST") && url.equals("/user/signup")) {
                String messageBody = from.getMessagebody();
                addUserToDB(messageBody);
                response302Header(dos, "/index.html");
            }

            //login 버튼 누르면 user확인
            if (method.equals("POST") && url.equals("/user/login")) {
                String loginUserInfo = from.getMessagebody();
                String[] loginUserInfos = loginUserInfo.split("&");
                String[] userIdInfo = loginUserInfos[0].split("=");
                String[] userPasswordInfo = loginUserInfos[1].split("=");

                MemoryUserRepository getDB = MemoryUserRepository.getInstance();
                User loginuser = getDB.findUserById(userIdInfo[1]);
                //user 동일함
                if ((loginuser != null) && loginuser.getPassword().equals(userPasswordInfo[1])) {
                    response302HeaderWithCookie(dos, "/index.html");
                }
                response302Header(dos, "login_failed.html");
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void serveFile(String filePath, DataOutputStream dos) throws IOException {
        File file = new File(filePath);
        byte[] body = Files.readAllBytes(file.toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void addUserToDB(String query) {
        Map<String, String> userInfo = parseQueryParameter(query);
        String userId = userInfo.get("userId");
        String password = userInfo.get("password");
        String name = userInfo.get("name");
        String email = userInfo.get("email");
        User user = new User(userId, password, name, email);
        MemoryUserRepository db = MemoryUserRepository.getInstance(); //싱글톤
        db.addUser(user);
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            //Cookie: logined=true
            dos.writeBytes("Set-Cookie: logined=true; Path=/;\r\n"); //...d00ffe2c"; logined=true - 이런식으로 붙음
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path);
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