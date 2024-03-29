package http.message;

public enum HttpMethod {

    Get("GET");


    final private String Method;



    public String getMethod(){
        return Method;
    }
    HttpMethod(String method) {
        Method = method;
    }
}
