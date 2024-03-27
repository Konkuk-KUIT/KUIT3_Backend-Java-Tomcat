package webserver;

import controller.*;

import java.util.HashMap;
import java.util.Map;

import static webserver.UrlPath.*;

public class RequestMapper {
    private final HttpRequest request;
    private final HttpResponse response;
    private Map<String, Controller> controllerMap;

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
        initControllerMap();
    }
    private void initControllerMap(){
        controllerMap = new HashMap<>();
        controllerMap.put(HOME.getPath(), new HomeController());
        controllerMap.put(SIGNUP.getPath(), new SignUpController());
        controllerMap.put(LOGIN.getPath(), new LoginController());
        controllerMap.put(LIST.getPath(), new ListController());
    }

    public void proceed() throws Exception {
        try {
            String url = request.getPath();
            Controller controller = controllerMap.get(url);

            controller.execute(request, response);

        } catch (Exception e) {
            throw new RuntimeException("Request processing failed", e);
        }
    }
}
