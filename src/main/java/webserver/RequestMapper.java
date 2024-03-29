package webserver;

import webserver.controller.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final Map<String, Controller> controllers;

    public RequestMapper() {
        this.controllers = new HashMap<>();
        initializeControllers();
    }
    private void initializeControllers() {
        controllers.put("/", new HomeController());
        controllers.put("/user/signup", new SignUpController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/userList", new ListController());
    }

    public void proceed(HttpRequest req, HttpResponse res) throws IOException {
        String url = req.getUrl();
        Controller controller = controllers.getOrDefault(url, new ForwardController());
        controller.execute(req, res);
    }
}
