package webserver;

import constant.*;
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

                if (line.startsWith(HttpHeader.CONTENT_LENGTH.getType())) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                if (line.startsWith(HttpHeader.COOKIE.getType())) {
                    cookie = line.split(": ")[1];
                }
            }

            if(method.equals(HttpMethod.GET.get())) {
                respondGetMethod(url, dos, cookie);
            } else if(method.equals(HttpMethod.POST.get())) {
                String body = IOUtils.readData(br, requestContentLength);
                respondPostMethod(url, dos, body);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void respondGetMethod(String url, DataOutputStream dos, String cookie) {
        String[] urls = url.split("\\?");
        if(urls[0].endsWith(".css")) {
            try {
                byte[] body = Files.readAllBytes(Paths.get(URL.getFileRoot() + urls[0]));
                response200HeaderCss(dos, body.length);
                responseBody(dos, body);
            } catch (IOException e) {
                log.log(Level.SEVERE,e.getMessage());
            }
        }

        if(urls[0].equals(URL.INDEX_HTML.getRequestUrl()) || urls[0].equals("/")) {
            try {
                byte[] body = Files.readAllBytes(Paths.get(URL.INDEX_HTML.getFilePath()));
                response200Header(dos, body.length);
                responseBody(dos, body);
            } catch (IOException e) {
                log.log(Level.SEVERE,e.getMessage());
            }
            return;
        }
        if(urls[0].equals(URL.FORM_HTML.getRequestUrl())) {
            try {
                byte[] body = Files.readAllBytes(Paths.get(URL.FORM_HTML.getFilePath()));
                response200Header(dos, body.length);
                responseBody(dos, body);
            } catch (IOException e) {
                log.log(Level.SEVERE,e.getMessage());
            }
            return;
        }
        if(urls[0].equals(URL.SIGN_UP.getRequestUrl())) {
            try {
                Map<String, String> query = HttpRequestUtils.parseQueryParameter(urls[1]);
                User user = new User(query.get(UserQuery.USER_ID.getQuery()),
                        query.get(UserQuery.PASSWORD.getQuery()),
                        query.get(UserQuery.NAME.getQuery()),
                        query.get(UserQuery.EMAIL.getQuery()));
                MemoryUserRepository.getInstance().addUser(user);
                log.log(Level.INFO, user.getUserId());

                byte[] body = Files.readAllBytes(Paths.get(URL.INDEX_HTML.getFilePath()));
                response200Header(dos, body.length);
                responseBody(dos, body);
            } catch (IOException e) {
                log.log(Level.SEVERE,e.getMessage());
            }
            return;
        }
        if(urls[0].equals(URL.LOGIN_HTML.getRequestUrl())) {
            try {
                byte[] body = Files.readAllBytes(Paths.get(URL.LOGIN_HTML.getFilePath()));
                response200Header(dos, body.length);
                responseBody(dos, body);
            } catch (IOException e) {
                log.log(Level.SEVERE,e.getMessage());
            }
            return;
        }
        if(urls[0].equals(URL.LOGIN_FAILED_HTML.getRequestUrl())) {
            try {
                byte[] body = Files.readAllBytes(Paths.get(URL.LOGIN_FAILED_HTML.getFilePath()));
                response200Header(dos, body.length);
                responseBody(dos, body);
            } catch (IOException e) {
                log.log(Level.SEVERE,e.getMessage());
            }
            return;
        }
        if(urls[0].equals(URL.USER_LIST.getRequestUrl())) {
            try {
                if(!cookie.startsWith("logined=true")) {
                    response302Header(dos, URL.LOGIN_HTML.getRequestUrl());
                    return;
                }
                byte[] body = Files.readAllBytes(Paths.get(URL.USER_LIST_HTML.getFilePath()));
                response200Header(dos, body.length);
                responseBody(dos, body);
            } catch (IOException e) {
                log.log(Level.SEVERE,e.getMessage());
            }
        }
    }

    private void respondPostMethod(String url, DataOutputStream dos, String body) {
        if(url.equals(URL.SIGN_UP.getRequestUrl())) {
            Map<String, String> query = HttpRequestUtils.parseQueryParameter(body);
            User user = new User(query.get(UserQuery.USER_ID.getQuery()),
                    query.get(UserQuery.PASSWORD.getQuery()),
                    query.get(UserQuery.NAME.getQuery()),
                    query.get(UserQuery.EMAIL.getQuery()));
            MemoryUserRepository.getInstance().addUser(user);
            log.log(Level.INFO, user.getUserId());

            response302Header(dos, URL.INDEX_HTML.getRequestUrl());
            return;
        }
        if(url.equals(URL.LOGIN.getRequestUrl())) {
            Map<String, String> query = HttpRequestUtils.parseQueryParameter(body);
            User login = new User(query.get(UserQuery.USER_ID.getQuery()),
                    query.get(UserQuery.PASSWORD.getQuery()),
                    query.get(UserQuery.NAME.getQuery()),
                    query.get(UserQuery.EMAIL.getQuery()));
            User user = MemoryUserRepository.getInstance().findUserById(login.getUserId());
            if(user != null && user.getPassword().equals(login.getPassword())) {
                response302HeaderWithCookie(dos, URL.INDEX_HTML.getRequestUrl());
            } else {
                response302Header(dos, URL.LOGIN_FAILED_HTML.getRequestUrl());
            }
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes(StatusCode.Status302.getHeader());
            dos.writeBytes(HttpHeader.LOCATION.getHeader(path));
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes(StatusCode.Status302.getHeader());
            dos.writeBytes(HttpHeader.LOCATION.getHeader(path));
            dos.writeBytes(HttpHeader.SET_COOKIE.getHeader("logined=true"));
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200HeaderCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes(StatusCode.Status200.getHeader());
            dos.writeBytes(HttpHeader.CONTENT_TYPE.getHeader("text/css;charset=utf-8"));
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.getHeader(lengthOfBodyContent));
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes(StatusCode.Status200.getHeader());
            dos.writeBytes(HttpHeader.CONTENT_TYPE.getHeader("text/html;charset=utf-8"));
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.getHeader(lengthOfBodyContent));
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