package webserver;

public enum UserQueryKey {

    USER_ID("userId"),
    USER_PASSWORD("password"),
    USER_EMAIL("email"),
    USER_NAME("name");

    final private String Key;



    public String getKey(){
        return Key;
    }
    UserQueryKey(String Key) {
        this.Key = Key;
    }
}
