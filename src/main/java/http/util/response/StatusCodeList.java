package http.util.response;

public enum StatusCodeList {
    STATUS_200(200),
    STATUS_302(302);

    private int status_code;

    StatusCodeList(int status_code){
        this.status_code = status_code;
    }


}
