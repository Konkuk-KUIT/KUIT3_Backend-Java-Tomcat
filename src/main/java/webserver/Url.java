package webserver;

public enum Url {
    ROOT_URL("./webapp"),
    HOME_URL ("/index.html"),
    LOGIN_FAILED_URL("/user/login_failed.html"),
    LIST_URL ("/user/list.html"),
    LOGIN_URL("/user/login.html");

    final private String Url;
    public String getUrl(){
        return Url;
    }

    Url(String url) {
        Url = url;
    }
}
