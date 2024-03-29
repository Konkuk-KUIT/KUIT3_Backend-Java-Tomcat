package webserver;

import enumModel.HTTPMethod;
import enumModel.Path_enum;
import enumModel.UserQuery;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static db.MemoryUserRepository.memoryUserRepository;

public class Httprequest {

    private final BufferedReader br;
    private final String method;
    private final String url;
    private final String version;

    private int requestContentLength;
    private String cookie;


    public Httprequest(BufferedReader br) throws IOException {
        this.br = br;
        String startLine = this.br.readLine();
        System.out.println(startLine);
        String[] startLines = startLine.split(" ");
        this.method = startLines[0];
        this.url = startLines[1];
        this.version = startLines[2];
        while (true) {
            final String line = this.br.readLine();
            System.out.println(line);
            if (line.equals("")) {
                break;
            }
            // header info
            if (line.startsWith("Content-Length")) {
                this.requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }
            if (line.startsWith("Cookie")) {
                this.cookie = line.split(": ")[1];
            }
        }
    }

    public String getMethod() {
        System.out.println(this.method);
        return method;
    }

    public String getUrl() {
        System.out.println(this.url);
        return url;
    }

    public String getCookie() {
        return cookie;
    }

    public int getRequestContentLength() {
        return requestContentLength;
    }

    public static Httprequest from(BufferedReader br) throws IOException {
        Httprequest hq = new Httprequest(br);
        return hq;
    }
    private Path getPath(){
        return Paths.get(Path_enum.DEFAULT_PATH.getPath() + url);
    }
    public byte[] makeRequest() throws IOException {
        if (method.equals(HTTPMethod.GET.getHttpMethod()) && url.endsWith(".html")) {
            return Files.readAllBytes(getPath());
        }
        if(url.equals(Path_enum.SIGN_UP_URL.getPath())){
            //body 부분
            String UserInfoquery = IOUtils.readData(br,requestContentLength);
            //System.out.println(UserInfoquery);
            Map<String,String> UserMap = HttpRequestUtils.parseQueryParameter(UserInfoquery);
            User user = new User(UserMap.get(UserQuery.USERID.getUserquery()),UserMap.get(UserQuery.PASSWORD.getUserquery()),UserMap.get(UserQuery.NAME.getUserquery()),UserMap.get(UserQuery.EMAIL.getUserquery()));
            memoryUserRepository.addUser(user);
            response302Header(dos,Path_enum.HOME_PATH.getPath());
        }
        if(url.equals(Path_enum.LOGIN_URL.getPath())){
            String loginUserQuery = IOUtils.readData(br,requestContentLength);
            System.out.println(loginUserQuery);
            Map<String,String> UserMap = HttpRequestUtils.parseQueryParameter(loginUserQuery);
            if(memoryUserRepository.findUserById(UserMap.get(UserQuery.USERID.getUserquery()))==null){ //없는 경우
                response302Header(dos,Path_enum.LOGIN_FAILED_PATH.getPath());
                return;
            }
            response302HeaderWithCookie(dos,Path_enum.HOME_PATH.getPath());
        }
        if(hq.getUrl().equals(Path_enum.LIST_URL.getPath())){
            if(!cookie.contains("logined=true")){
                response302Header(dos,Path_enum.LOGIN_PATH.getPath());
                return;
            }
            Path userListPath = Paths.get(Path_enum.getFullPath(Path_enum.LIST_PATH.getPath()));
            body = Files.readAllBytes(userListPath);
        }
        if (method.equals(HTTPMethod.GET.getHttpMethod()) && url.endsWith(".css")) {
            return Files.readAllBytes(getPath());
            response200HeaderWithCss(dos, body.length);
            responseBody(dos, body);
            return;
        }
        return new byte[0]; //수정 필요
    }
}
