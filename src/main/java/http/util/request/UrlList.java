package http.util.request;

public enum UrlList {
    BASIC_URL("./webapp"),
    INDEX_URL("/index.html"),
    USERLIST_URL("/user/list.html"),
    LOGIN_URL("/user/login.html"),
    LOGIN_FAILED_URL("/user/login_failed.html");

    private String url;

    UrlList(String url){
        this.url = url;
    }


    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return url;
    }
}
