package http.request;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestTest {
    private final File GET_FILE = new File("src/test/resource/Http_GET.txt");
    private final File POST_FILE = new File("src/test/resource/Http_POST.txt");

    @Test
    void HttpRequest_GET_방식_테스트() {
        // given
        try (InputStream in = new FileInputStream(GET_FILE)) {

            //when
            HttpRequest httpRequest = HttpRequest.from(in);

            //then
            assertEquals("GET", httpRequest.getMethod());
            assertEquals("/user/create?userId=ms&password=0000&name=minseok", httpRequest.getPath());
            assertEquals("HTTP/1.1", httpRequest.getVersion());

            assertEquals("localhost:8080", httpRequest.getHeaderLine("Host"));
            assertEquals("keep-alive", httpRequest.getHeaderLine("Connection"));
            assertEquals("40", httpRequest.getHeaderLine("Content-Length"));
            assertEquals("*/*", httpRequest.getHeaderLine("Accept"));

            assertEquals("ms", httpRequest.getParameter("userId"));
            assertEquals("0000", httpRequest.getParameter("password"));
            assertEquals("minseok", httpRequest.getParameter("name"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void HttpRequest_POST_방식_테스트() {
        // given
        try (InputStream in = new FileInputStream(POST_FILE)) {

            //when
            HttpRequest httpRequest = HttpRequest.from(in);

            //then
            assertEquals("POST", httpRequest.getMethod());
            assertEquals("/user/create", httpRequest.getPath());
            assertEquals("HTTP/1.1", httpRequest.getVersion());

            assertEquals("localhost:8080", httpRequest.getHeaderLine("Host"));
            assertEquals("keep-alive", httpRequest.getHeaderLine("Connection"));
            assertEquals("40", httpRequest.getHeaderLine("Content-Length"));
            assertEquals("*/*", httpRequest.getHeaderLine("Accept"));

            assertEquals("jw", httpRequest.getParameter("userId"));
            assertEquals("password", httpRequest.getParameter("password"));
            assertEquals("jungwoo", httpRequest.getParameter("name"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}