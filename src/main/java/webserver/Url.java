package webserver;

public enum Url {
    USER_LIST("/user/userList"),
    SIGNUP("/user/signup"),
    LOGIN("/user/login.html");
    
    private String url;

    Url(String url) {
        this.url = url;
    }

    public String get(){
        return this.url;
    }
}
