package webserver;

import javax.swing.text.html.CSS;

public enum Url {
    ROOT_URL("./webapp"),
    HOME_URL ("/index.html"),
    LOGIN_FAILED_URL("/user/login_failed.html"),
    LIST_URL ("/user/list.html"),
    USER_LIST("/user/userList"),
    USER_SIGNUP("/user/signup"),
    LOGIN_URL_HTML("/user/login.html"),
    LOGIN_URL("/user/login"),
    CSS(".css"),
    HTML(".html");

    final private String Url;
    public String getUrl(){
        return Url;
    }

    Url(String url) {
        Url = url;
    }
}
