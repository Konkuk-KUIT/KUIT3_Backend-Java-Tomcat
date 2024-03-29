package webserver;

import http.util.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {

    private BufferedReader bufferedReaderFromFile(String filePath) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filePath))));
    }

    @Test
    public void httpRequestFromFileTest() throws IOException {
        //given
        String testDirectory = "src/test/resources/";
        String testFile = "request.txt";

        //when
        BufferedReader br = bufferedReaderFromFile(testDirectory + testFile);
        HttpRequest httpRequest = HttpRequest.from(br);

        //then
        assertEquals("/user/create", httpRequest.getPath());
        assertEquals("POST", httpRequest.getMethod());
        assertEquals(40, httpRequest.getContentLength());
        Assertions.assertEquals("userId=jw&password=password&name=jungwoo", IOUtils.readData(br, httpRequest.getContentLength()));
    }
}