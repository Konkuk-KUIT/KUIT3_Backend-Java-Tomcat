package Controllers;

import ControllerClass.HTTPController;
import request.request;
import response.response;

public class userListController extends HTTPController {


    @Override
    public void doPOST(request req, response res){
        System.out.println();
    }
    @Override
    public void doGET(request req, response res){
        String key = req.getHeader("Cookie");

        if(key!=null && key.equals("logined=true")){
            res.forward("/webapp/user/list.html");
        }else{
            res.redirect("/index");
        }
    }

}
