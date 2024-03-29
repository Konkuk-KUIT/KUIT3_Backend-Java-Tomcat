package domain;

import java.lang.reflect.Method;

public enum HttpMethod {
    POST("POST"),
    DELETE("DELETE"),
    PUT("PUT"),
    GET("GET");

    private final String method;

    HttpMethod(String method){
        this.method = method;
    }

    public String getMethod(){
        return method;
    }
}
