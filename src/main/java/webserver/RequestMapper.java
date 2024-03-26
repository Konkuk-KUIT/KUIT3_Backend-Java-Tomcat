package webserver;

import webserver.controller.*;
import webserver.httprequest.HttpRequest;
import webserver.httpresponse.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static webserver.httprequest.UrlPath.*;

public class RequestMapper {
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private Controller controller;
    Map<String, Controller> controllers = new HashMap<>(){{
        put(ROOT.getPath(), new HomeController());
        put(INDEX.getPath(), new ForwardController());
        put(USER_FORM.getPath(), new UserFormController());
        put(USER_SIGNUP.getPath(), new SignUpController());
        put(USER_LOGIN_FILE.getPath(), new LoginFormController());
        put(USER_LOGIN.getPath(), new LoginController());
        put(USER_LIST.getPath(), new ListController());
        put(CSS.getPath(), new CssController());
    }};

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.controller = controllers.get(httpRequest.getUrl());
    }

    void proceed() throws IOException {
        controller.excute(httpRequest,httpResponse);
    }
}
