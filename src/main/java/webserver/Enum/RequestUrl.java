package webserver.Enum;

public enum RequestUrl {
    LIST_HTML("/user/list.html"),
    LIST("/user/userList"),
    LOGIN("/user/login.html"),
    LOGIN_POST("/user/login"),
    LOGIN_FAIL("/user/login_failed.html"),
    SIGNUP("/user/signup"),
    ROOT("./webapp"),
    HOME("/index.html");

    private String url;

    RequestUrl(String url) {

        this.url = url;
    }

    public String getUrl() {

        return url;
    }
}
