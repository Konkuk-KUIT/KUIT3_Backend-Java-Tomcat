package ControllerClass;
import request.request;
import response.response;
public interface Controller {
    public abstract void execute(request req,response res);

}
