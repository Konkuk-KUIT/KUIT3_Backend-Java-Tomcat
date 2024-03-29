package webserver;

public enum QueryKey {
    USER_ID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    private String key;

    QueryKey(String key) {
        this.key = key;
    }

    public String get(){
        return this.key;
    }
}
