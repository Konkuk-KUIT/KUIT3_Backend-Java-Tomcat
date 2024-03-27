package webserver;

import db.MemoryUserRepository;
import enumModel.HTTPMethod;
import enumModel.Path_enum;
import enumModel.UserQuery;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import javax.print.DocFlavor;
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
    private final MemoryUserRepository memoryUserRepository;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
        this.memoryUserRepository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        //String url;
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);
            // Header 분석
            Httprequest hq = Httprequest.from(br);
//            String startLine = br.readLine();
//            //System.out.println(startLine);
//            String[] startLines = startLine.split(" ");
//            String method = startLines[0];
//            url = startLines[1];

//            int requestContentLength = 0;
//            String cookie = "";

//            while (true) {
//                final String line = br.readLine();
//                if (line.equals("")) {
//                    break;
//                }
//                // header info
//                if (line.startsWith("Content-Length")) {
//                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
//                }
//                if (line.startsWith("Cookie")) {
//                    cookie = line.split(": ")[1];
//                    System.out.println(cookie);
//                }
//            }
            Path path = Paths.get(Path_enum.DEFAULT_PATH.getPath() + hq.getUrl());
            byte[] body = new byte[0];
            if (hq.getMethod().equals(HTTPMethod.GET.getHttpMethod()) && hq.getUrl().endsWith(".html")) {
                body = Files.readAllBytes(path);
            }
            if(hq.getUrl().equals(Path_enum.SIGN_UP_URL.getPath())){
                //body 부분
                String UserInfoquery = IOUtils.readData(br,hq.getRequestContentLength());
                //System.out.println(UserInfoquery);
                Map<String,String> UserMap = HttpRequestUtils.parseQueryParameter(UserInfoquery);
                User user = new User(UserMap.get(UserQuery.USERID.getUserquery()),UserMap.get(UserQuery.PASSWORD.getUserquery()),UserMap.get(UserQuery.NAME.getUserquery()),UserMap.get(UserQuery.EMAIL.getUserquery()));
                memoryUserRepository.addUser(user);
                response302Header(dos,Path_enum.HOME_PATH.getPath());
            }
            if(hq.getUrl().equals(Path_enum.LOGIN_URL.getPath())){
                String loginUserQuery = IOUtils.readData(br,hq.getRequestContentLength());
                System.out.println(loginUserQuery);
                Map<String,String> UserMap = HttpRequestUtils.parseQueryParameter(loginUserQuery);
                if(memoryUserRepository.findUserById(UserMap.get(UserQuery.USERID.getUserquery()))==null){ //없는 경우
                    response302Header(dos,Path_enum.LOGIN_FAILED_PATH.getPath());
                    return;
                }
                response302HeaderWithCookie(dos,Path_enum.HOME_PATH.getPath());
            }
            if(hq.getUrl().equals(Path_enum.LIST_URL.getPath())){
                if(!hq.getCookie().contains("logined=true")){
                    response302Header(dos,Path_enum.LOGIN_PATH.getPath());
                    return;
                }
                Path userListPath = Paths.get(Path_enum.getFullPath(Path_enum.LIST_PATH.getPath()));
                body = Files.readAllBytes(userListPath);
            }
            if (hq.getMethod().equals(HTTPMethod.GET.getHttpMethod()) && hq.getUrl().endsWith(".css")) {
                body = Files.readAllBytes(path);
                response200HeaderWithCss(dos, body.length);
                responseBody(dos, body);
                return;
            }
            response200Header(dos, body.length);
            responseBody(dos, body);

        }
        catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200HeaderWithCss(DataOutputStream dos, int length) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + length + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: "+ path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true" + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header (DataOutputStream dos,int lengthOfBodyContent){
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
            System.out.println(path);
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void responseBody (DataOutputStream dos,byte[] body){
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}