package webserver.enums;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum UrlPath {
    ROOT("./webapp"),
    HOME("/"),
    INDEX("/index.html"),
    USER_FORM("/user/form.html"),
    LOGIN_HTML("/user/login.html"),
    LOGIN("/user/login"),
    SIGNUP("/user/signup"),
    LOGIN_FAILED_HTML("/user/login_failed.html"),
    LIST("/user/userList"),
    CSS("/css/styles.css");


    private final String path;

    UrlPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static Path getHomePath() {
        return Paths.get(ROOT.getPath() + INDEX.getPath());
    }
}
