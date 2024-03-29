package webserver;

import controller.*;

import java.util.HashMap;
import java.util.Map;

import static webserver.enums.UrlPath.*;

public class RequestMapper {
    private HttpRequest request;
    private HttpResponse response;
    private Map<String, Controller> controllerMap;

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
        initControllerMap();
    }

    private void initControllerMap(){
        controllerMap = new HashMap<>();
        controllerMap.put(HOME.getPath(), new HomeController());
        controllerMap.put(INDEX.getPath(), new ForwardController());
        controllerMap.put(USER_FORM.getPath(), new UserFormController());
        controllerMap.put(SIGNUP.getPath(), new SignUpController());
        controllerMap.put(LOGIN_HTML.getPath(), new LoginHtmlController());
        controllerMap.put(LOGIN.getPath(), new LoginController());
        controllerMap.put(LOGIN_FAILED_HTML.getPath(),new LoginHtmlController());
        controllerMap.put(LIST.getPath(), new ListController());
        controllerMap.put(CSS.getPath(), new CssController());
    }

    public void proceed() throws Exception {
        Controller controller = controllerMap.get(request.getPath());
        controller.execute(request, response);
    }
}
