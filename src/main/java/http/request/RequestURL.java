package http.request;

public enum RequestURL {
    ROOT("./webapp"),
    HOME_URL("/index.html"),
    LOGIN_FAILED_URL("/user/login_failed.html"),
    LOGIN_URL("/user/login.html"),
    LIST_URL("/user/list.html"),
    SIGN_UP("/user/signup"),
    LOGIN("/user/login"),
    USER_LIST("/user/userList");

    private final String url;

    RequestURL(String url) {
        this.url = url;
    }

    public String get() {
        return url;
    }
}
