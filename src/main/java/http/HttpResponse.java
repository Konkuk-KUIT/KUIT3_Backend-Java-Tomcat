package http;

import java.io.DataOutputStream;
import structure.Body;
import structure.Header;
import structure.StartLine;

public class HttpResponse {
    private final byte[] response;
    private final byte[] body;

    public HttpResponse(byte[] response, byte[] body) {
        this.response = response;
        this.body = response;
    }

    public byte[] getResponse() {
        return this.response;
    }

    public byte[] getBody() {
        return this.body;
    }
}
