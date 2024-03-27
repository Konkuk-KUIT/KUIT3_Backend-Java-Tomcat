package webserver;

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
    LOGIN_FAILED("/user/login_failed.html"),
    LIST("/user/list.html");

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
