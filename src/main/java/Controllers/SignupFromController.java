package Controllers;

import ControllerClass.HTTPController;
import model.User;
import request.request;
import response.response;

import java.util.Map;

public class SignupFromController  extends HTTPController {


    @Override
    public void doPOST(request req, response res){
        System.out.println();
    }
    @Override
    public void doGET(request req, response res){
        System.out.println("index doGET start");
        res.forward(req.getPath());
        res.printResponse();
    }

}
