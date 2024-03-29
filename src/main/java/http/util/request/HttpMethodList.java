package http.util.request;

public enum HttpMethodList {

    GET("GET"),
    POST("POST");

    private String method;

    HttpMethodList(String method){
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
