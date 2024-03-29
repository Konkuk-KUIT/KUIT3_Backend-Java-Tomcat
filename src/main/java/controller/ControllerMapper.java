package controller;

import http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {

    public ControllerMapper() {
        init();
    }

    private final Map<String, Controller> controllers = new HashMap<>();  // Make it singleton

    // initializing
    private void init() {
        controllers.put("/", new HomeController());
        controllers.put("/index.html", new HomeController());
        controllers.put("/user/signup", new SignUpController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/css/styles.css", new CssController());
        controllers.put("/user/userList", new UserController());
    }

    public Controller getController(HttpRequest httpRequest) {
        System.out.println(httpRequest.parsePath());
        return controllers.get(httpRequest.parsePath());   // 여기서 exception 터지면 그 path에 상응하는 Controller가 없다느 거
    }
}
