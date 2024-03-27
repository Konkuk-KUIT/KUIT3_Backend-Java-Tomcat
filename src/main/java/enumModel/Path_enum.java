package enumModel;

public enum Path_enum {
    DEFAULT_PATH("C:/kuit/KUIT3_Backend-Java-Tomcat/webapp/"),
    HOME_PATH("index.html"),
    LOGIN_PATH("user/login.html"),
    LIST_PATH("user/list.html"),
    FORM_PATH("user/list.html"),
    LOGIN_FAILED_PATH("user/login_failed.html"),

    SIGN_UP_URL("/user/signup"),
    LOGIN_URL("/user/login"),
    ;

    private final String path;
    Path_enum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
    public String getFullPath(Path_enum pe) {
        return DEFAULT_PATH.getPath()+pe.getPath();
    }
}
