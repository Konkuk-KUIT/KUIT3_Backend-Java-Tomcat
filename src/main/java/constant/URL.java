package constant;

public enum URL {

    INDEX_HTML("/index.html"),
    FORM_HTML("/user/form.html"),
    SIGN_UP("/user/signup"),
    LOGIN_HTML("/user/login.html"),
    LOGIN("/user/login"),
    LOGIN_FAILED_HTML("/login_failed.html"),
    USER_LIST("/user/userList"),
    USER_LIST_HTML("/user/userList.html");

    private final String url;
    private static final String fileRoot = "webapp";

    URL(String url) {
        this.url = url;
    }

    public String getRequestUrl() {
        return url;
    }

    public String getFilePath() {
        return fileRoot + url;
    }

    public static String getFileRoot() {
        return fileRoot + "/";
    }

}
