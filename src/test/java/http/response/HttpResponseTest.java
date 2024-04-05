package http.response;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {
    private final String testDirectory = "./src/test/resources/";
    private final String forwardPath = "Http_Forward.txt";
    private final String redirectPath = "Http_Response_Redirect.txt";

    @Test
    void forward() throws IOException {
        OutputStream out = Files.newOutputStream(Paths.get(testDirectory + forwardPath));
        HttpResponse httpResponse = new HttpResponse(out);

        httpResponse.forward("/index.html");
    }

    @Test
    void redirect() throws IOException {
        OutputStream out = Files.newOutputStream(Paths.get(testDirectory + redirectPath));
        HttpResponse httpResponse = new HttpResponse(out);

        httpResponse.redirect("/index.html");
    }
}