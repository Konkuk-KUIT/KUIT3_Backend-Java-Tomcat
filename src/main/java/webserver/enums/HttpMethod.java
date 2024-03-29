package webserver.enums;

public enum HttpMethod {
    GET("GET"),
    POST("POST");


    final String method;

    HttpMethod(String method) {
        this.method = method;
    }
    public String getMethod() {
        return method;
    }
}