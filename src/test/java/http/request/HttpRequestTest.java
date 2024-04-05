package http.request;

import http.constants.HttpMethod;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestTest {
    private final String testDirectory = "./src/test/resources/";
    private final String getPath = "HttpGetWithQuery.txt";
    private final String postPath = "HttpPostWithQuery.txt";

    @Test
    void HTTP_GET_Query() throws IOException {
        InputStream in = Files.newInputStream(Paths.get(testDirectory + getPath));
        HttpRequest httpRequest = HttpRequest.from(in);

        assertEquals("/user/create?userId=encoreJeong&password=password&name=jaeyeon", httpRequest.getPath());
        assertEquals(HttpMethod.GET.get(), httpRequest.getMethod());
        assertEquals("encoreJeong", httpRequest.getQueryParameter("userId"));
        assertEquals("password", httpRequest.getQueryParameter("password"));
        assertEquals("jaeyeon", httpRequest.getQueryParameter("name"));
        // assert fields
        assertEquals("localhost:8080", httpRequest.getField("Host"));
        assertEquals("keep-alive", httpRequest.getField("Connection"));
        assertEquals("*/*", httpRequest.getField("Accept"));
    }

    @Test
    void HTTP_POST_Query() throws IOException {
        InputStream in = Files.newInputStream(Paths.get(testDirectory + postPath));
        HttpRequest httpRequest = HttpRequest.from(in);

        assertEquals("/user/create", httpRequest.getPath());
        assertEquals(HttpMethod.POST.get(), httpRequest.getMethod());
        assertEquals("encoreJeong", httpRequest.getQueryParameter("userId"));
        assertEquals("password", httpRequest.getQueryParameter("password"));
        assertEquals("jaeyeon", httpRequest.getQueryParameter("name"));
        // assert fields
        assertEquals("localhost:8080", httpRequest.getField("Host"));
        assertEquals("keep-alive", httpRequest.getField("Connection"));
        assertEquals("49", httpRequest.getField("Content-Length"));
        assertEquals("*/*", httpRequest.getField("Accept"));
    }
}