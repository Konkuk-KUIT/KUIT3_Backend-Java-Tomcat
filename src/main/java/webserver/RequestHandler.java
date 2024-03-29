package webserver;

import db.MemoryUserRepository;
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

    private final String GET = "GET";
    private final String POST = "POST";
    private final String INDEX_HTML = "/index.html";
    private final String FORM_HTML = "/user/form.html";
    private final String SIGN_UP = "/user/signup";
    private final String LOGIN_HTML = "/user/login.html";
    private final String LOGIN = "/user/login";
    private final String LOGIN_FAILED_HTML = "/login_failed.html";
    private final String USER_LIST = "/user/userList";

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
            String method = startLines[0];
            String url = startLines[1];
            log.log(Level.INFO, String.valueOf(url));

            int requestContentLength = 0;
            String cookie = "";

            while (true) {
                final String line = br.readLine();
                if (line.isEmpty()) {
                    break;
                }

                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                if (line.startsWith("Cookie")) {
                    cookie = line.split(": ")[1];
                }
            }

            switch (method) {
                case GET -> respondGetMethod(url, dos, cookie);
                case POST -> {
                    String body = IOUtils.readData(br, requestContentLength);
                    respondPostMethod(url, dos, body);
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void respondGetMethod(String url, DataOutputStream dos, String cookie) {
        String[] urls = url.split("\\?");
        if(urls[0].endsWith(".css")) {
            try {
                byte[] body = Files.readAllBytes(Paths.get("webapp/" + urls[0]));
                response200HeaderCss(dos, body.length);
                responseBody(dos, body);
            } catch (IOException e) {
                log.log(Level.SEVERE,e.getMessage());
            }
        }
        switch (urls[0]) {
            case INDEX_HTML, "/" -> {
                try {
                    byte[] body = Files.readAllBytes(Paths.get("webapp/index.html"));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } catch (IOException e) {
                    log.log(Level.SEVERE,e.getMessage());
                }
            }
            case FORM_HTML -> {
                try {
                    byte[] body = Files.readAllBytes(Paths.get("webapp/user/form.html"));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } catch (IOException e) {
                    log.log(Level.SEVERE,e.getMessage());
                }
            }
            case SIGN_UP -> {
                try {
                    Map<String, String> query = HttpRequestUtils.parseQueryParameter(urls[1]);
                    User user = new User(query.get("userId"), query.get("password"), query.get("name"), query.get("email"));
                    MemoryUserRepository.getInstance().addUser(user);
                    log.log(Level.INFO, user.getUserId());

                    byte[] body = Files.readAllBytes(Paths.get("webapp/index.html"));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } catch (IOException e) {
                    log.log(Level.SEVERE,e.getMessage());
                }
            }
            case LOGIN_HTML -> {
                try {
                    byte[] body = Files.readAllBytes(Paths.get("webapp/user/login.html"));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } catch (IOException e) {
                    log.log(Level.SEVERE,e.getMessage());
                }
            }
            case LOGIN_FAILED_HTML -> {
                try {
                    byte[] body = Files.readAllBytes(Paths.get("webapp/user/login_failed.html"));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } catch (IOException e) {
                    log.log(Level.SEVERE,e.getMessage());
                }
            }
            case USER_LIST -> {
                try {
                    if(!cookie.startsWith("logined=true")) {
                        response302Header(dos, INDEX_HTML);
                        return;
                    }
                    byte[] body = Files.readAllBytes(Paths.get("webapp/user/list.html"));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } catch (IOException e) {
                    log.log(Level.SEVERE,e.getMessage());
                }
            }
        }
    }

    private void respondPostMethod(String url, DataOutputStream dos, String body) {
        switch (url) {
            case SIGN_UP -> {
                Map<String, String> query = HttpRequestUtils.parseQueryParameter(body);
                User user = new User(query.get("userId"), query.get("password"), query.get("name"), query.get("email"));
                MemoryUserRepository.getInstance().addUser(user);
                log.log(Level.INFO, user.getUserId());

                response302Header(dos, INDEX_HTML);
            }
            case LOGIN -> {
                Map<String, String> query = HttpRequestUtils.parseQueryParameter(body);
                User login = new User(query.get("userId"), query.get("password"), query.get("name"), query.get("email"));
                User user = MemoryUserRepository.getInstance().findUserById(login.getUserId());
                if(user != null && user.getPassword().equals(login.getPassword())) {
                    response302HeaderWithCookie(dos, INDEX_HTML);
                } else {
                    response302Header(dos, LOGIN_FAILED_HTML);
                }
            }
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location:" + path + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true" + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200HeaderCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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