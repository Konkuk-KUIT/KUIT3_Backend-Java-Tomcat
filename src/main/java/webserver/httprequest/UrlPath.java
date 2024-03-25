package webserver.httprequest;

public enum UrlPath {
    INDEX("/index.html"),
    USER_FORM("/user/form.html"),
    USER_SIGNUP("/user/signup"),
    USER_LOGIN_FILE("/user/login.html"),
    USER_LOGIN("/user/login"),
    USER_LIST("/user/userList"),
    ROOT("./webapp"),
    LOGIN_FAILED("/login_failed.html");

    private final String path;

    UrlPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
