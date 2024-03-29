package http.util;

public enum HttpStatusCode3XX implements HttpStatusCode{

    Redirect("302 Redirect");
    private String statusCode;

    HttpStatusCode3XX(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getValue() {
        return null;
    }
}
