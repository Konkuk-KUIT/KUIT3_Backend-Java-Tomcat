package http.request;

public enum RequestURL {
    ROOT_URL("./webapp"),
    HOME_URL("/index.html"),
    LOGIN_FAILED_URL("/user/login_failed.html"),
    LOGIN_URL("/user/login.html"),
    LIST_URL("/user/list.html");

    private final String url;

    RequestURL(String url) {
        this.url = url;
    }

    public String get() {
        return url;
    }
}
