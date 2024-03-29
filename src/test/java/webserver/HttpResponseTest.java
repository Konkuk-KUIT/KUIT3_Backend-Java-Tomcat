package webserver;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

class HttpResponseTest {

    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }

    @Test
    void testForward() throws IOException {
        String testDirectory = "src/test/resources/";
        String forwardPath = "webapp/index.html";

        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory + forwardPath));

        httpResponse.forward(testDirectory+forwardPath);
    }
    @Test
    void testRedirect() throws IOException {
        String testDirectory = "src/test/resources/";
        String forwardPath = "webapp/index.html";

        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory+forwardPath));

        httpResponse.redirect(testDirectory+forwardPath);
    }

}