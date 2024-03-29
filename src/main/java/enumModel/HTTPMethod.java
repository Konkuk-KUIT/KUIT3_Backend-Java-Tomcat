package enumModel;

public enum HTTPMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    HEAD("HEAD");
    private final String httpMethod;
    HTTPMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public static HTTPMethod findHttpMethod(String method) {//map에 넣어서 해보기
        if(method.equals("GET"))
            return HTTPMethod.GET;
        if(method.equals("POST"))
            return HTTPMethod.POST;
        if(method.equals("PATCH"))
            return HTTPMethod.PATCH;
        if(method.equals("PUT"))
            return HTTPMethod.PUT;
        if(method.equals("DELETE"))
            return HTTPMethod.DELETE;
        return null;
    }
}
