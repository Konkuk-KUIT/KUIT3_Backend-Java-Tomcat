package ControllerClass;

import request.request;
import response.response;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class HTTPController implements Controller {
    private static final Logger log = Logger.getLogger(HTTPController.class.getName());
    /*private request req;
    private response res;*/
    public void execute(request req,response res){
        if(req.getMethod().equals("GET")){
            doGET(req,res);
            return;
        }
        if(req.getMethod().equals("POST")){
            doPOST(req,res);
            return;
        }
        log.log(Level.SEVERE, "invalid Method");

    }



    abstract public void doPOST(request req, response res);
    abstract public void doGET(request req, response res);

}
