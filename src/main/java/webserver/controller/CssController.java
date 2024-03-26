package webserver.controller;

import webserver.httprequest.HttpRequest;
import webserver.httpresponse.HttpResponse;

import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;
import static webserver.httprequest.UrlPath.FILE_ROOT;

public class CssController implements Controller{
    @Override
    public void excute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.setBody(readAllBytes(Paths.get(FILE_ROOT.getPath() + httpRequest.getUrl())));
        httpResponse.response200HeaderWithCss();
        httpResponse.responseBody();
    }
}
