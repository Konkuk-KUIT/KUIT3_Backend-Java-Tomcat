package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.PageURL;

import java.io.IOException;

public class ListController implements Controller{

    @Override
    public void execute(HttpRequest req, HttpResponse res) throws IOException {

        if (!req.getCookie().equals("logined=true")) {
            res.redirect(PageURL.LOGIN.getFullPath());
            return;
        }
        res.forward(PageURL.LIST.getFullPath());
    }
}
