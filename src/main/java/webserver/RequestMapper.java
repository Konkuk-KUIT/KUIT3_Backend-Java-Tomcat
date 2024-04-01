package webserver;

import controller.*;
import http.request.HttpRequest;
import http.request.RequestURL;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest request;
    private final HttpResponse response;
    private Map<String, Controller> controllers = new HashMap<>();
    private Controller controller;

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
        initControllers();
        controller = controllers.get(request.getPath());
    }

    private void initControllers() {
        controllers.put("/", new HomeController());
        controllers.put(RequestURL.HOME_URL.get(), new ForwardController());
        controllers.put(RequestURL.SIGN_UP.get(), new SignUpController());
        controllers.put(RequestURL.LOGIN.get(), new LoginController());
        controllers.put(RequestURL.USER_LIST.get(), new ListController());
    }

    public void proceed() throws IOException {
        if (controller != null) {
            controller.execute(request, response);
            return;
        }
        // controllers 에 해당 경로가 없어서 매핑이 안 된 경우
        response.forward(request.getPath());
    }
}
