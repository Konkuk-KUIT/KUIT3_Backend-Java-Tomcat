package http.request;

import http.constants.HttpMethod;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {

    private final String testDirectory = "./src/test/resources/";
    private final String getPath = "HttpGetWithQuery.txt";
    private final String postPath = "HttpPostWithQuery.txt";

    @Test
    void HTTP_GET_Query() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + getPath));

        assertEquals("/user/create", httpRequest.getUrl());
        assertEquals(HttpMethod.GET, httpRequest.getMethod());
        assertEquals("encoreJeong", httpRequest.getQueryParameter("userId"));
        assertEquals("password", httpRequest.getQueryParameter("password"));
        assertEquals("jaeyeon", httpRequest.getQueryParameter("name"));
    }

    @Test
    void HTTP_POST_Query() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + postPath));
        Map<String, String> queryParametersFromBody = httpRequest.getQueryParametersFromBody();
        assertEquals("encoreJeong", queryParametersFromBody.get("userId"));
        assertEquals("password", queryParametersFromBody.get("password"));
        assertEquals("jaeyeon", queryParametersFromBody.get("name"));
        assertEquals("/user/create", httpRequest.getUrl());
        assertEquals(HttpMethod.POST, httpRequest.getMethod());
    }

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

}