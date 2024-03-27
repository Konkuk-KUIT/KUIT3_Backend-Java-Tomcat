package enumModel;

public enum UserQuery {
    USERID("userId"),

    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");
    private final String userquery;


    UserQuery(String userquery) {
        this.userquery = userquery;
    }

    public String getUserquery() {
        return userquery;
    }
}
