package webserver;
import ControllerClass.Controller;
import request.request;
import response.response;
import ControllerClass.HTTPController;
import Controllers.*;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {
    private static Map<String, Controller> controllers = new HashMap<String, Controller>();;
    private Controller targetController;
    /*static {
        controllers.put("/index", new indexController());
        controllers.put("/index.html", new indexController());
    }*/

    public ControllerMapper(){
        System.out.println("Mapper start");
        init();

    }

    public void init(){
        controllers.put("/", new indexController());
        controllers.put("/index", new indexController());
        controllers.put("/index.html", new indexController());
        controllers.put("/webapp/user/form.html", new SignupFromController());
        controllers.put("/user/signup", new SignupController());
        controllers.put("/webapp/user/login.html", new LoginController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/userList", new userListController());
        System.out.println("Mapper end");
    }
    public boolean execute(request req,response res){
        targetController = controllers.get(req.getPath());
        if(targetController==null){
            System.out.println("target URL null");
        }
        targetController.execute(req,res);
        return true;
    }
    /*public Controller getController(){
        return controllers.get(req)
    }*/
}
