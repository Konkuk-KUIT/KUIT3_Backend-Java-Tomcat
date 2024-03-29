package domain;

public enum URL {
    ROOT_URL ("./webapp"),
    HOME_URL ("/index.html"),
    LOGIN_FAILED_URL ("/user/login_failed.html"),
    LOGIN_URL ("/user/login.html"),
    LIST_URL ("/user/list.html");

    private final String value;

    URL(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
