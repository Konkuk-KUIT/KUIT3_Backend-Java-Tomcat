package enums;

public enum HttpMethod {
    GET("get"),
    POST("post");

    private final String method;

    HttpMethod(String method){
        this.method = method;
    }

    public String getMethod(){
        return method;
    }
}
