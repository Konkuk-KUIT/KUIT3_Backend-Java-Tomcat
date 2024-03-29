package webserver.Enum;


public enum HttpStatus {
    OK("OK"),
    REDIRECT("Found");

    private final String status;

    HttpStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
