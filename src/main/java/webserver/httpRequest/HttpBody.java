package webserver.httpRequest;

import domain.URL;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

public class HttpBody {
    private byte[] body = new byte[0];
    private String url;

    public HttpBody(String url) throws IOException {
        this.body = Files.readAllBytes(Paths.get(url));
    }
}
