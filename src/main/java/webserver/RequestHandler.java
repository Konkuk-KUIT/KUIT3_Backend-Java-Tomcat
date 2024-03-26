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
import static webserver.HttpMethod.GET;
import static webserver.HttpMethod.POST;
import static webserver.UrlPath.*;
import static webserver.UserQueryKey.*;


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
            HttpResponse httpResponse = new HttpResponse(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            String url = httpRequest.getPath();
            String method = httpRequest.getMethod();

            byte[] body = new byte[0];

            // 요구 사항 1
            if (httpRequest.getMethod().equals(GET.getMethod()) && httpRequest.getPath().equals(INDEX.getPath())) {
                Path path = Paths.get(ROOT.getPath() + url);
                body = Files.readAllBytes(path);
            }

            if (url.equals("/")) {
                body = Files.readAllBytes(getHomePath());
            }
            //요구 사항 2
            if(method.equals(GET.getMethod()) && url.equals(USER_FORM.getPath())){
                Path path = Paths.get(ROOT.getPath() + url);
                body = Files.readAllBytes(path);
            }
            if(method.equals(GET.getMethod()) && url.startsWith(SIGNUP.getPath())){
                String UserInfo = url.split("\\?")[1];
                Map<String,String> queryParameter = parseQueryParameter(UserInfo);
                String userId = queryParameter.get(USER_ID.getKey());
                String password = queryParameter.get(PASSWORD.getKey());
                String name = queryParameter.get(NAME.getKey());
                String email = queryParameter.get(EMAIL.getKey());
                User user = new User(userId, password, name, email);
                repository.addUser(user);
                httpResponse.response302Header(INDEX.getPath());
            }
            //요구 사항 3
            if(method.equals(POST.getMethod()) && url.equals(SIGNUP.getPath())){
                String queryString = IOUtils.readData(br, httpRequest.getContentLength());
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                String userId = queryParameter.get(USER_ID.getKey());
                String password = queryParameter.get(PASSWORD.getKey());
                String name = queryParameter.get(NAME.getKey());
                String email = queryParameter.get(EMAIL.getKey());
                User user = new User(userId, password, name, email);
                repository.addUser(user);
                httpResponse.response302Header(INDEX.getPath());
            }
            //요구 사항 5
            if (url.equals(LOGIN.getPath())) {
                String queryString = IOUtils.readData(br, httpRequest.getContentLength());
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                String userId = queryParameter.get(USER_ID.getKey());
                User user = repository.findUserById(userId);
                if (user != null && user.getPassword().equals(queryParameter.get(PASSWORD.getKey()))) {
                    httpResponse.response302HeaderWithCookie(INDEX.getPath());
                    return;
                }
                httpResponse.response302Header(LOGIN_FAILED.getPath());

                return;
            }

            // 요구 사항 6
            if (url.equals("/user/userList")) {
                if (!httpRequest.getCookie().equals("logined=true")) {
                    httpResponse.response302Header(LOGIN.getPath());
                    return;
                }
                body = Files.readAllBytes(Paths.get(ROOT.getPath() + LIST.getPath()));
            }

            // 요구 사항 7번
            if (method.equals(GET.getMethod()) && url.endsWith(".css")) {
                body = Files.readAllBytes(Paths.get(ROOT.getPath() + url));
                httpResponse.response200HeaderWithCss(body.length);
                httpResponse.responseBody(body);
                return;
            }

            // image
            if (method.equals(GET.getMethod()) && url.endsWith(".jpeg")) {
                body = Files.readAllBytes(Paths.get(ROOT.getPath() + url));
                httpResponse.response200Header(body.length);
                httpResponse.responseBody(body);
                return;
            }

            httpResponse.response200Header(body.length);
            httpResponse.responseBody(body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }
}