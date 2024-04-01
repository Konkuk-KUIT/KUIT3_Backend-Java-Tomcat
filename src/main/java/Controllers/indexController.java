package Controllers;

import ControllerClass.Controller;
import ControllerClass.HTTPController;
import request.request;
import response.response;

public class indexController extends HTTPController {


    @Override
    public void doPOST(request req, response res){
        System.out.println();
    }
    @Override
    public void doGET(request req, response res){
        System.out.println("index doGET start");
        res.forward("/index.html");
        res.printResponse();
    }

}
