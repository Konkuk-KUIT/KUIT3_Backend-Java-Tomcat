package http.util;

import HttpResponse.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpResponseTest {
    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }

    @Test
    void create200Response() throws IOException {
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile("./src/test/java/resources/HttpResponseMessage"));

        httpResponse.forward("./webapp/index.html");
    }

    @Test
    void create302Response() throws IOException {
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile("./src/test/java/resources/HttpResponseMessage"));

        httpResponse.redirect("./webapp/user/login.html");
    }
}
