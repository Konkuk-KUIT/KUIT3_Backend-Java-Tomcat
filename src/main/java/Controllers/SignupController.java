package Controllers;

import ControllerClass.HTTPController;
import db.MemoryUserRepository;
import model.User;
import request.request;
import response.response;

import java.nio.file.Paths;
import java.util.Map;

public class SignupController extends HTTPController {
    private MemoryUserRepository DB= MemoryUserRepository.getInstance();

    @Override
    public void doPOST(request req, response res){
        DB.addUser(getUser(req.getAllBodyQuery()));
        res.redirect("/index.html");

        System.out.println("--------User---------\n");
        for(User user:DB.findAll()){
            System.out.println("["+user.getUserId()+","+user.getName()+","+user.getPassword()+","+user.getEmail());
        }
        System.out.println("--------User end---------\n");
    }
    @Override
    public void doGET(request req, response res){
        Map<String,String> query=req.getParamsAllQuery();
        if(query!=null && !query.isEmpty()) {
            User user = getUser(query);
            DB.addUser(user);
        }else{
            System.out.println("query is null");
        }
        res.redirect("/index");
        res.printResponse();
    }


    private User getUser(Map<String,String> userMap){
        User user = new User(userMap.get("userId"),userMap.get("password"),userMap.get("name"),userMap.get("email"));
        return user;
    }
}
