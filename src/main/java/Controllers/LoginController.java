package Controllers;

import model.User;
import ControllerClass.HTTPController;
import db.MemoryUserRepository;
import request.request;
import response.response;

public class LoginController extends HTTPController {
    private MemoryUserRepository DB= MemoryUserRepository.getInstance();
    @Override
    public void doGET(request req, response res){
        System.out.println("login API start");

        res.forward(req.getPath());
        res.printResponse();
    }
    @Override
    public void doPOST(request req, response res){
        User user = DB.findUserById(req.getBodyQuery("userId"));
        if(user==null){
            loginFail(res);
            return;
        }
        if(!req.getBodyQuery("password").equals(user.getPassword())){
            loginFail(res);
            return;
        }

        res.setHeader("Set-Cookie","logined=true; Path=/");
        res.redirect("/index.html");

    }

    public void loginFail(response res){
        res.forward("/webapp/user/login_failed.html");
    }
    public void loginSuccess(){

    }

}
