package webserver.controller;

import webserver.httprequest.HttpRequest;
import webserver.httpresponse.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;
import static webserver.httprequest.UrlPath.*;

public class ListController implements Controller{
    @Override
    public void excute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if(!(httpRequest.getCookie().startsWith("logined=true"))){
            httpResponse.response302Header(USER_LOGIN_FILE.getPath());
        }
        httpResponse.setBody(readAllBytes(Paths.get(ROOT.getPath()+"/user/list.html")));
        httpResponse.response200Header();
        httpResponse.responseBody();
    }
}
