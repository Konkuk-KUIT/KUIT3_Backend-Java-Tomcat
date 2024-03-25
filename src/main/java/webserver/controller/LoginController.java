package webserver.controller;

import db.MemoryUserRepository;
import http.util.IOUtils;
import model.User;
import webserver.httprequest.HttpRequest;
import webserver.httpresponse.HttpResponse;

import java.io.IOException;
import java.util.Map;

import static http.util.HttpRequestUtils.parseQueryParameter;
import static webserver.httprequest.UrlPath.INDEX;
import static webserver.httprequest.UrlPath.LOGIN_FAILED;

public class LoginController implements Controller{
    @Override
    public void excute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String queryString = IOUtils.readData(httpRequest.getBr(), httpRequest.getContentLength());
        Map<String,String> m = parseQueryParameter(queryString);
        User user = MemoryUserRepository.getInstance().findUserById(m.get("userId"));
        if (user != null && user.getPassword().equals(m.get("password"))) {
            httpResponse.response302HeaderWithLogin(INDEX.getPath());
            return;
        }
        httpResponse.response302Header(LOGIN_FAILED.getPath());
    }
}
