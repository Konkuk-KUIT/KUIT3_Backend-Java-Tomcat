package webserver;

public enum PageURL {
    ROOT("./webapp"),
    HOME("/index.html"),
    LOGIN_FAILED("/user/login_failed.html"),
    LOGIN("/user/login.html"),
    LIST("/user/list.html");

    private final String url;

    PageURL(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    public String getFullPath() {
        if (this == ROOT) {
            return url;
        }
        return ROOT.url + url;
    }
    public static String getFullPath(String url) {
        return ROOT.url + url;
    }
}
