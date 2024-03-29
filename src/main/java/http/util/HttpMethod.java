package http.util;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private String methodType;

    HttpMethod(String methodType) {
        this.methodType = methodType;
    }

    public String getMethodType() {
        return methodType;
    }
}