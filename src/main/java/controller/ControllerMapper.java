package controller;

import http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {

    // TODO : 나중에 따로 등록하는 객체 따로 뺴라, 얘 HashMap 안됨? 맘에 안들어, samePath but, different HTTP Method..?
    // TODO : 근데 여기서 HashMap으로 하면 path 등록을 Mapper에서 해야됨 <- 별론듯) ㄴㄴ 순회하면서 HashMap에 등록 해줘도 될 듯 근데 그러면 controller가 path를 알려줘야되


    public ControllerMapper() {
        init();
    }

    private final Map<String, Controller> controllers = new HashMap<>();  // Make it singleton

    // initializing
    private void init() {
        controllers.put("/", new HomeController());
        controllers.put("/index.html", new HomeController());
    }

    public Controller getController(HttpRequest httpRequest) {
        return controllers.get(httpRequest.parsePath());   // 여기서 exception 터지면 그 path에 상응하는 Controller가 없다느 거
    }

}
